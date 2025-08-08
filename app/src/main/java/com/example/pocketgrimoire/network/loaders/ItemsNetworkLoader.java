package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.mappers.ItemMappers;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.entities.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Produces a mapped list of Items with all fields set, network only (no database writes)
 *
 * Requires DndApiService to expose:
 * - Single<ResourceListDto> listEquipment()
 * - Single<ResourceListDto> listEquipmentCategories()
 * - Single<EquipmentCategoryRequestDto> getEquipmentCategory(String index)
 */
public final class ItemsNetworkLoader {

    private final DndApiService api;

    public ItemsNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /**
     * Fetch all equipment categories then map: equipmentIndex -> categoryName.
     *
     * @return Single that emits the map equipmentIndex -> categoryName
     */
    public Single<Map<String, String>> buildEquipmentIndexToCategoryMap() {
        return api.listEquipmentCategories()
                // Run the API call on the IO scheduler
                .subscribeOn(Schedulers.io())
                .flatMap(list -> {
                    // If the API returns null or has no results, use an empty list
                    List<ResourceRefDto> refs = list != null ? list.results : new ArrayList<>();
                    // Turn the list into a stream. For each category reference, make another API call
                    return Flowable.fromIterable(refs)
                            .flatMap(ref ->
                                            // network call per category
                                            api.getEquipmentCategory(ref.index)
                                                    // each call also on IO pool
                                                    .subscribeOn(Schedulers.io())
                                                    .toFlowable(),
                                    /* maxConcurrency */ 12
                            )
                            // return all category responses as a Single<List<String>>
                            .toList();
                })
                // Convert the list of category details into a map: equipmentIndex -> categoryName
                .map(ItemMappers::buildIndexToCategoryMap);
    }

    /**
     * Fetch all equipment, resolve each to a category via the provided map,
     * then map to Items entities (in-memory only).
     *
     * @param equipmentIndexToCategory map equipmentIndex -> categoryName
     * @return Single that emits the complete list of Items (name, category, isEquippable)
     */
    public Single<List<Items>> loadAllItemsWithCategory(Map<String, String> equipmentIndexToCategory) {
        return api.listEquipment()
                // Run the API call on the IO scheduler
                .subscribeOn(Schedulers.io())
                // Turn the API list result into a stream of ResourceRefDto.
                // If the list or its results are null, stream an empty list instead of failing.
                .flatMapPublisher(list ->
                        io.reactivex.rxjava3.core.Flowable.fromIterable(
                                (list != null && list.results != null)
                                        ? list.results
                                        : java.util.Collections.<ResourceRefDto>emptyList()
                        )
                )
                // For each equipment ref, find its category name using the lookup map,
                // then map it into our Items entity with isEquippable computed by the mapper
                .map(ref -> {
                    String category = equipmentIndexToCategory.get(ref.index);
                    return ItemMappers.fromEquipmentRef(ref, category);
                })
                // Drop any null or empty entries that slipped through.
                .filter(item -> item != null && item.getName() != null && !item.getName().isEmpty())
                // Gather all mapped entities back into a Single<List<Items>>.
                .toList();
    }
}
