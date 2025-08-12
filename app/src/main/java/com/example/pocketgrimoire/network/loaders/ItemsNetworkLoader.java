package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.mappers.ItemMappers;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Pure-network loader for Items.
 * 1) Fetch master equipment list
 * 2) Build index→categoryName via equipment-categories
 * 3) Join + compute isEquippable
 */
public final class ItemsNetworkLoader {
    private final DndApiService api;

    public ItemsNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /** Defensive helper: return list results or empty list. */
    private static List<ResourceRefDto> safeRefs(ResourceListDto list) {
        return (list != null && list.results != null) ? list.results : Collections.emptyList();
    }

    /**
     * Build a map equipmentIndex → category display name by enumerating all categories.
     */
    private Single<HashMap<String, String>> buildIndexToCategoryName() {
        final int MAX_CONCURRENCY = 6;

        return api.listEquipmentCategories()
                .subscribeOn(Schedulers.io())
                .toFlowable()
                .flatMap(list -> Flowable.fromIterable(safeRefs(list)))
                .flatMap(
                        catRef -> api.getEquipmentCategory(catRef.index)
                                .subscribeOn(Schedulers.io())
                                .toFlowable(),
                        /* delayErrors */ false,
                        /* maxConcurrency */ MAX_CONCURRENCY
                )
                .collect(
                        HashMap<String, String>::new,
                        ItemMappers::putCategoryMemberships
                );
    }

    /**
     * Fetch all equipment and join with category names.
     *
     * @return Single<List<Items>> ready for seeding
     */
    public Single<List<Items>> fetchAll() {
        return Single.zip(
                api.listEquipment().subscribeOn(Schedulers.io()),
                buildIndexToCategoryName(),
                (equipmentList, indexToCategory) -> {
                    List<Items> out = new ArrayList<>();
                    for (ResourceRefDto ref : safeRefs(equipmentList)) {
                        String catName = (ref != null) ? indexToCategory.get(ref.index) : null;
                        Items row = ItemMappers.fromEquipmentRef(ref, catName);
                        if (row != null) out.add(row);
                    }
                    return out;
                }
        ).subscribeOn(Schedulers.io());
    }
}
