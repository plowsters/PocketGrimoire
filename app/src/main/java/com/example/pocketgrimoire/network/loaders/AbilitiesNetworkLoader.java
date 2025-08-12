package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.mappers.AbilityMappers;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

/**
 * Loads Abilities (Features + Traits) using only list endpoints that already exist.
 * - Features => traitOrFeat = false
 * - Traits   => traitOrFeat = true
 *
 * We dedupe by (name + kind) in first-seen order and return a single list.
 * Availability lists are left empty here (can be filled later without changing DAO/Seeder).
 */
public final class AbilitiesNetworkLoader {
    private final DndApiService api;

    public AbilitiesNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /** Fetch both features and traits, merge, and return as a single list. */
    public Single<List<Abilities>> fetchAll() {
        Single<List<Abilities>> features = fetchFeaturesFromList();
        Single<List<Abilities>> traits   = fetchTraitsFromList();

        return Single.zip(features, traits, (f, t) -> {
            Map<String, Abilities> byKey = new LinkedHashMap<>();
            for (Abilities a : safeList(f)) putIfAbsent(byKey, a);
            for (Abilities a : safeList(t)) putIfAbsent(byKey, a);
            return new ArrayList<>(byKey.values());
        });
    }

    /** Build Abilities (feature) rows directly from the list endpoint. */
    private Single<List<Abilities>> fetchFeaturesFromList() {
        // DndApiService: Single<ResourceListDto>
        return api.listFeatures()
                .map((ResourceListDto listDto) -> {
                    List<ResourceRefDto> refs =
                            (listDto != null && listDto.results != null)
                                    ? listDto.results
                                    : Collections.emptyList();

                    Map<String, Abilities> acc = new LinkedHashMap<>();
                    for (ResourceRefDto r : refs) {
                        if (r == null || r.name == null) continue;
                        Abilities a = AbilityMappers.fromParts(
                                r.name,
                                /* isTrait */ false,
                                /* availableToClass */ null,
                                /* availableToRace  */ null
                        );
                        if (a != null) putIfAbsent(acc, a);
                    }
                    return new ArrayList<>(acc.values());
                });
    }

    /** Build Abilities (trait) rows directly from the list endpoint. */
    private Single<List<Abilities>> fetchTraitsFromList() {
        // DndApiService: Single<ResourceListDto>
        return api.listTraits()
                .map((ResourceListDto listDto) -> {
                    List<ResourceRefDto> refs =
                            (listDto != null && listDto.results != null)
                                    ? listDto.results
                                    : Collections.emptyList();

                    Map<String, Abilities> acc = new LinkedHashMap<>();
                    for (ResourceRefDto r : refs) {
                        if (r == null || r.name == null) continue;
                        Abilities a = AbilityMappers.fromParts(
                                r.name,
                                /* isTrait */ true,
                                /* availableToClass */ null,
                                /* availableToRace  */ null
                        );
                        if (a != null) putIfAbsent(acc, a);
                    }
                    return new ArrayList<>(acc.values());
                });
    }

    // ---------- helpers ----------

    private static <T> List<T> safeList(List<T> in) {
        return (in != null) ? in : Collections.emptyList();
    }

    private static void putIfAbsent(Map<String, Abilities> acc, Abilities a) {
        // key by name + kind (T/F) to avoid mixing features/traits with same name
        String key = a.getName() + "|" + (a.isTraitOrFeat() ? "T" : "F");
        acc.putIfAbsent(key, a);
    }
}
