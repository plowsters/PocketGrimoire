package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.dto.SpellRequestDto;
import com.example.pocketgrimoire.database.mappers.SpellMappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class SpellsNetworkLoader {
    private final DndApiService api;

    public SpellsNetworkLoader(DndApiService api) { this.api = api; }

    private static List<ResourceRefDto> safeRefs(ResourceListDto list) {
        return (list != null && list.results != null) ? list.results : Collections.emptyList();
    }

    /** name → detailed spell (has level + school). */
    private Single<Map<String, SpellRequestDto>> gatherAllSpellDetails() {
        final int MAX_CONCURRENCY = 8;

        return api.listSpells()
                .subscribeOn(Schedulers.io())
                .toFlowable()
                .flatMap(list -> Flowable.fromIterable(safeRefs(list)))
                .flatMap(ref ->
                                api.getSpell(ref.index)
                                        .subscribeOn(Schedulers.io())
                                        .toFlowable(),
                        /* delayErrors */ false,
                        /* maxConcurrency */ MAX_CONCURRENCY
                )
                .collect(HashMap<String, SpellRequestDto>::new,
                        (acc, dto) -> {
                            if (dto != null && dto.name != null) {
                                acc.put(dto.name, dto);
                            }
                        });
    }

    /** spellName → classes (display names). */
    private Single<Map<String, List<String>>> gatherClassAvailability(Map<String, String> classes) {
        final int MAX_CONCURRENCY = 6;

        return Flowable.fromIterable(classes.entrySet())
                .flatMap(entry ->
                                api.listClassSpellsAtOrBelow(entry.getValue(), /* lvl */ 9)
                                        .subscribeOn(Schedulers.io())
                                        .toFlowable()
                                        .flatMap(list -> Flowable.fromIterable(safeRefs(list))
                                                .map(ref -> new Pair(ref.name, entry.getKey()))),
                        /* delayErrors */ false,
                        /* maxConcurrency */ MAX_CONCURRENCY
                )
                .collect(HashMap<String, List<String>>::new,
                        (acc, pair) -> {
                            if (pair.first == null || pair.second == null) return;
                            List<String> lst = acc.computeIfAbsent(pair.first, k -> new ArrayList<>());
                            lst.add(pair.second);
                        });
    }

    /** Fully materialize Spells for seeding. */
    public Single<List<Spells>> fetchAll(Map<String, String> classes) {
        return Single.zip(
                gatherAllSpellDetails(),
                gatherClassAvailability(classes),
                (byName, availability) -> {
                    List<Spells> out = new ArrayList<>(byName.size());
                    for (SpellRequestDto dto : byName.values()) {
                        if (dto == null || dto.name == null) continue;
                        List<String> avail = availability.get(dto.name);
                        if (avail == null) avail = Collections.emptyList();
                        String schoolName = (dto.school != null) ? dto.school.name : null;

                        Spells s = SpellMappers.fromNameLevelSchoolAndAvailability(
                                dto.name, dto.level, schoolName, avail);
                        if (s != null && s.getName() != null && !s.getName().isEmpty()) {
                            out.add(s);
                        }
                    }
                    return out;
                }
        ).subscribeOn(Schedulers.io());
    }

    /** Tiny tuple to avoid Map.Entry APIs. */
    private static final class Pair {
        final String first;
        final String second;
        Pair(String first, String second) { this.first = first; this.second = second; }
    }
}
