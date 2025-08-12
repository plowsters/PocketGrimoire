package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.mappers.ItemMappers;
import com.example.pocketgrimoire.database.remote.dto.ApiRef;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.DndApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public final class ItemsNetworkLoader {
    private final DndApiService api;

    public ItemsNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /**
     * Strategy:
     * 1) Get equipment categories (list of ResourceRefDto, which usually have only url+name).
     * 2) Fetch each category detail (EquipmentCategoryRequestDto) concurrently (capped).
     * 3) For each category, fold its equipment list into a name→Items map (keeps first-seen, stable order).
     * 4) Map to List<Items>.
     */
    public Single<List<Items>> fetchAll() {
        return api.listEquipmentCategories()
                .flatMap(catListDto -> {
                    final List<ResourceRefDto> cats =
                            (catListDto != null && catListDto.results != null)
                                    ? catListDto.results
                                    : Collections.emptyList();

                    return Flowable.fromIterable(cats)
                            .flatMap(
                                    // Each category detail; ResourceRefDto often lacks "index", so derive from URL.
                                    catRef -> api.getEquipmentCategory(safeIndexFromUrl(catRef)).toFlowable(),
                                    /* delayErrors */ false,
                                    /* maxConcurrency */ 4
                            )
                            .collect(
                                    // Use LinkedHashMap to preserve first-seen order.
                                    () -> new LinkedHashMap<String, Items>(),
                                    (acc, catDto) -> {
                                        if (catDto == null) return;

                                        final String catName = catDto.name;
                                        final List<ApiRef> equipmentRefs =
                                                (catDto.equipment != null) ? catDto.equipment : Collections.emptyList();

                                        for (ApiRef eq : equipmentRefs) {
                                            if (eq == null || eq.name == null) continue;
                                            final String key = eq.name;
                                            // Keep first category that mentions this item (cheap + stable).
                                            if (!acc.containsKey(key)) {
                                                Items item = ItemMappers.fromEquipmentRef(eq, catName);
                                                if (item != null) acc.put(key, item);
                                            }
                                        }
                                    }
                            )
                            .map((Map<String, Items> map) -> new ArrayList<>(map.values()));
                });
    }

    /** Extract the last path segment from a URL like ".../api/equipment-categories/simple-weapons". */
    private static String safeIndexFromUrl(ResourceRefDto r) {
        if (r == null || r.url == null) return null;
        String u = r.url.trim();
        if (u.isEmpty()) return null;
        int slash = u.lastIndexOf('/');
        return (slash >= 0 && slash + 1 < u.length()) ? u.substring(slash + 1) : u;
    }
}