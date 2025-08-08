package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.mappers.SpellMappers;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.dto.SpellRequestDto;
import com.example.pocketgrimoire.database.entities.Spells;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Produces a mapped list of Spells with all fields set, network only (no database writes)
 *
 * Requires DndApiService to expose:
 * - Single<ResourceListDto> listSpells()
 * - Single<SpellRequestDto> getSpell(String index)
 */
public final class SpellsNetworkLoader {

    private final DndApiService api;

    public SpellsNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /**
     * Fetch all spells, then fetch details in parallel with bounded concurrency,
     * and map to Spells entities in memory.
     *
     * @return Single that emits the complete list of mapped Spells
     */
    public Single<List<Spells>> loadAllSpells() {
        return api.listSpells()
                // Perform API request in IO scheduler
                .subscribeOn(Schedulers.io())
                .map(list ->
                        // If list is null, continue with empty list
                        (list != null && list.results != null)
                                ? list.results
                                : java.util.Collections.<ResourceRefDto>emptyList()
                )
                // Turn the list into a stream so we can fetch details for each spell.
                .flattenAsFlowable(refs -> refs)
                .flatMap(ref ->
                                // for each spell, perform network request on a background IO thread
                                api.getSpell(ref.index)
                                        .subscribeOn(Schedulers.io())
                                        .toFlowable(),
                        /* maxConcurrency */ 12
                )
                // Map the detail DTO into our Spells entity (name, level, school, classes).
                .map(SpellMappers::fromDetail)
                // Drop nulls and empty names.
                .filter(spell -> spell != null && spell.getName() != null && !spell.getName().isEmpty())
                // Collect the stream back into a Single<List<Spells>>.
                .toList();
    }
}
