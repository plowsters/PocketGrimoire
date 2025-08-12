package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.mappers.AbilityMappers;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Pure network loader for Abilities (features & traits).
 * <p>
 * Workflow:
 * <ol>
 *   <li>Gather the set of all ability names from /features and /traits.</li>
 *   <li>Gather class availability for each feature and race availability for each trait.</li>
 *   <li>Merge into a list of {@link Abilities} using {@link AbilityMappers}.</li>
 * </ol>
 */
public final class AbilitiesNetworkLoader {
    private final DndApiService api;

    public AbilitiesNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /** Defensive helper: return list results or empty list. */
    private static List<ResourceRefDto> safeRefs(ResourceListDto list) {
        return (list != null && list.results != null) ? list.results : Collections.emptyList();
    }

    /**
     * Fetch the universe of ability names and their kind (feature=true, trait=false).
     *
     * @return Single mapping ability name → isFeature (true if feature, false if trait)
     */
    private Single<Map<String, Boolean>> gatherKinds() {
        Single<Map<String, Boolean>> features = api.listFeatures()
                .subscribeOn(Schedulers.io())
                .map(list -> {
                    Map<String, Boolean> out = new HashMap<>();
                    for (ResourceRefDto r : safeRefs(list)) {
                        if (r != null && r.name != null) out.put(r.name, /* isTrait */ false);
                    }
                    return out;
                });

        Single<Map<String, Boolean>> traits = api.listTraits()
                .subscribeOn(Schedulers.io())
                .map(list -> {
                    Map<String, Boolean> out = new HashMap<>();
                    for (ResourceRefDto r : safeRefs(list)) {
                        if (r != null && r.name != null) out.put(r.name, /* isTrait */ true);
                    }
                    return out;
                });

        return Single.zip(features, traits, (f, t) -> {
            Map<String, Boolean> merged = new HashMap<>(f);
            // If a name appears in both (rare), keep existing (feature=false) unless trait=true arrives.
            for (Map.Entry<String, Boolean> e : t.entrySet()) {
                merged.put(e.getKey(), e.getValue()); // trait=true overwrites feature=false
            }
            return merged;
        });
    }

    /**
     * Build abilityName → list of classes (display names) that provide that feature.
     *
     * @param classes map of displayName → classIndex (e.g., "Cleric" → "cleric")
     * @return Single mapping ability name → list of class display names
     */
    private Single<Map<String, List<String>>> gatherClassAvailability(Map<String, String> classes) {
        final int MAX_CONCURRENCY = 6;

        return Flowable.fromIterable(classes.entrySet())
                .flatMap(entry ->
                                api.listClassFeatures(entry.getValue())
                                        .subscribeOn(Schedulers.io())
                                        .toFlowable()
                                        .flatMap(list -> Flowable.fromIterable(safeRefs(list))
                                                .map(ref -> new Pair(ref.name, entry.getKey()))),
                        /* delayErrors */ false,
                        /* maxConcurrency */ MAX_CONCURRENCY
                )
                .collect(HashMap<String, List<String>>::new, (acc, pair) -> {
                    if (pair.first == null || pair.second == null) return;
                    List<String> lst = acc.computeIfAbsent(pair.first, k -> new ArrayList<>());
                    if (!lst.contains(pair.second)) lst.add(pair.second);
                });
    }

    /**
     * Build abilityName → list of races (display names) that provide that trait.
     *
     * @param races map of displayName → raceIndex (e.g., "Dwarf" → "dwarf")
     * @return Single mapping ability name → list of race display names
     */
    private Single<Map<String, List<String>>> gatherRaceAvailability(Map<String, String> races) {
        final int MAX_CONCURRENCY = 6;

        return Flowable.fromIterable(races.entrySet())
                .flatMap(entry ->
                                api.listRaceTraits(entry.getValue())
                                        .subscribeOn(Schedulers.io())
                                        .toFlowable()
                                        .flatMap(list -> Flowable.fromIterable(safeRefs(list))
                                                .map(ref -> new Pair(ref.name, entry.getKey()))),
                        /* delayErrors */ false,
                        /* maxConcurrency */ MAX_CONCURRENCY
                )
                .collect(HashMap<String, List<String>>::new, (acc, pair) -> {
                    if (pair.first == null || pair.second == null) return;
                    List<String> lst = acc.computeIfAbsent(pair.first, k -> new ArrayList<>());
                    if (!lst.contains(pair.second)) lst.add(pair.second);
                });
    }

    /**
     * Fully materialize Abilities for seeding (name, traitOrFeat, availableToClass, availableToRace).
     *
     * @param classes displayName → classIndex map used to build class availability
     * @param races   displayName → raceIndex map used to build race availability
     * @return Single of a list of Abilities entities to persist
     */
    public Single<List<Abilities>> fetchAll(Map<String, String> classes, Map<String, String> races) {
        return Single.zip(
                gatherKinds(),
                gatherClassAvailability(classes),
                gatherRaceAvailability(races),
                (kinds, byClass, byRace) -> {
                    List<Abilities> out = new ArrayList<>(kinds.size());
                    for (Map.Entry<String, Boolean> e : kinds.entrySet()) {
                        String name = e.getKey();
                        boolean isTrait = Boolean.TRUE.equals(e.getValue()); // NEW semantic
                        List<String> classAvail = byClass.getOrDefault(name, Collections.emptyList());
                        List<String> raceAvail  = byRace.getOrDefault(name, Collections.emptyList());
                        Abilities a = AbilityMappers.fromParts(name, isTrait, classAvail, raceAvail);
                        if (a != null) out.add(a);
                    }
                    return out;
                }
        ).subscribeOn(Schedulers.io());
    }

    /** Tiny tuple to avoid Map.Entry in streams. */
    private static final class Pair {
        final String first;
        final String second;
        Pair(String first, String second) { this.first = first; this.second = second; }
    }
}
