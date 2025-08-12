package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.mappers.SpellMappers;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.DndApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public final class SpellsNetworkLoader {
    private final DndApiService api;

    public SpellsNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /**
     * Strategy:
     * 1) List spells -> List<ApiRef>.
     * 2) Fetch each spell detail concurrently (capped).
     * 3) Map to Spells, dedupe by name with first-seen order.
     */
    public Single<List<Spells>> fetchAll() {
        return api.listSpells()
                .flatMap(listDto -> {
                    final List<ResourceRefDto> refs =
                            (listDto != null && listDto.results != null)
                                    ? listDto.results
                                    : Collections.emptyList();

                    return Flowable.fromIterable(refs)
                            .flatMap(
                                    ref -> api.getSpell(safeIndex(ref)).toFlowable(),
                                    /* delayErrors */ false,
                                    /* maxConcurrency */ 6
                            )
                            .collect(
                                    () -> new LinkedHashMap<String, Spells>(),
                                    (acc, dto) -> {
                                        if (dto == null) return;
                                        Spells s = SpellMappers.fromDetail(dto);
                                        if (s == null || s.getName() == null) return;
                                        // Keep first-seen spell by normalized name
                                        acc.putIfAbsent(s.getName(), s);
                                    }
                            )
                            .map((Map<String, Spells> map) -> new ArrayList<>(map.values()));
                });
    }

    private static String safeIndex(ResourceRefDto r) {
        return (r != null) ? r.index : null;
    }
}
