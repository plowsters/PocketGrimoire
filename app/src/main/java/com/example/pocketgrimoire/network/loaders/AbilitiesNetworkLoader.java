package com.example.pocketgrimoire.network.loaders;

import com.example.pocketgrimoire.database.mappers.AbilityMappers;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.entities.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Produces a mapped list of abilities, with all fields set
 *
 * Requires DndApiService to expose:
 * - Single<ResourceListDto> listFeatures()
 * - Single<ResourceListDto> listTraits()
 * - Single<ResourceListDto> listClassFeatures(String classIndex)
 * - Single<ResourceListDto> listRaceTraits(String raceIndex)
 */
public final class AbilitiesNetworkLoader {

    private final DndApiService api;

    public AbilitiesNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /**
     * Build the base Abilities list from the features and traits indexes.
     * Features map to traitOrFeat = false. Traits map to traitOrFeat = true.
     *
     * Threading:
     * - Each chain uses subscribeOn(IO) to run network work off the main thread.
     *
     * @return Single that emits a combined list of Abilities with empty availability lists
     */
    public Single<List<Abilities>> loadBaseAbilities() {
        // Features → Abilities with traitOrFeat = false
        Single<List<Abilities>> features = api.listFeatures()
                .subscribeOn(Schedulers.io())
                // If response or results is null, continue with an empty list
                .map(list ->
                        (list != null && list.results != null)
                                ? list.results
                                : java.util.Collections.<ResourceRefDto>emptyList()
                )
                // Stream refs so we can map them one by one
                .flattenAsFlowable(refs -> refs)
                // Map each ref to a base Abilities row (lists empty)
                .map(AbilityMappers::newFeature)
                // Drop any null/empty names
                .filter(a -> a != null && a.getName() != null && !a.getName().isEmpty())
                // Collect back into a list
                .toList();

        // Traits → Abilities with traitOrFeat = true
        Single<List<Abilities>> traits = api.listTraits()
                .subscribeOn(Schedulers.io())
                .map(list ->
                        (list != null && list.results != null)
                                ? list.results
                                : java.util.Collections.<ResourceRefDto>emptyList()
                )
                .flattenAsFlowable(refs -> refs)
                .map(AbilityMappers::newTrait)
                .filter(a -> a != null && a.getName() != null && !a.getName().isEmpty())
                .toList();

        // Zip the two lists into one combined list
        return Single.zip(features, traits, (f, t) -> {
            List<Abilities> out = new ArrayList<>(f.size() + t.size());
            out.addAll(f);
            out.addAll(t);
            return out;
        });
    }

    /**
     * Adds class names to features and race names to traits
     *
     * Notes:
     * - This is purely in-memory merging. No database writes here.
     * - We build a name|flag lookup to update the correct Abilities entries quickly.
     *
     * @param base list produced by loadBaseAbilities
     * @param classIndexToDisplay map of class index to display name (for example, cleric -> Cleric)
     * @param raceIndexToDisplay map of race index to display name (for example, dwarf -> Dwarf)
     * @return Single that emits a new Abilities list with availability fields merged
     */
    public Single<List<Abilities>> enrichAvailability(List<Abilities> base,
                                                      Map<String, String> classIndexToDisplay,
                                                      Map<String, String> raceIndexToDisplay) {
        // Build lookup by key "name|flag" for quick in-memory updates
        Map<String, Abilities> byKey = indexByNameAndFlag(base);

        // featureName -> [classDisplayName]
        Single<Map<String, List<String>>> featureToClasses = buildFeatureToClassesMap(classIndexToDisplay);
        // traitName   -> [raceDisplayName]
        Single<Map<String, List<String>>> traitToRaces = buildTraitToRacesMap(raceIndexToDisplay);

        // Merge both maps into the Abilities list and return a new list
        return Single.zip(featureToClasses, traitToRaces, (fc, tr) -> {
            // Merge class availability into features
            for (Map.Entry<String, List<String>> e : fc.entrySet()) {
                String featureName = e.getKey();
                Abilities ab = byKey.get(key(featureName, false));
                if (ab != null) {
                    ab.setAvailableToClass(AbilityMappers.mergeAvailableToClass(ab, e.getValue()));
                }
            }
            // Merge race availability into traits
            for (Map.Entry<String, List<String>> e : tr.entrySet()) {
                String traitName = e.getKey();
                Abilities ab = byKey.get(key(traitName, true));
                if (ab != null) {
                    ab.setAvailableToRace(AbilityMappers.mergeAvailableToRace(ab, e.getValue()));
                }
            }
            return new ArrayList<>(byKey.values());
        });
    }

    /**
     * Build a map featureName -> list of class display names that grant it.
     *
     * @param classIndexToDisplay map of class index to display name
     * @return Single that emits the map featureName -> class display names
     */
    public Single<Map<String, List<String>>> buildFeatureToClassesMap(
            Map<String, String> classIndexToDisplay
    ) {
        if (classIndexToDisplay == null || classIndexToDisplay.isEmpty()) {
            // Return an empty map as a Single so downstream chains don’t NPE.
            return Single.just(new LinkedHashMap<>());
        }

        return Flowable.fromIterable(classIndexToDisplay.entrySet())
                // For each class entry (index -> display name), fetch its features.
                // Use toFlowable() so we can set a concurrency cap on flatMap.
                .flatMap(en ->
                                api.listClassFeatures(en.getKey())
                                        .subscribeOn(Schedulers.io()) // each network call on IO
                                        .map(list -> toNameMultimapEntry(list, en.getValue())) // Map<String, List<String>>
                                        .toFlowable(),
                        /* maxConcurrency */ 12
                )
                // Merge a stream of small maps into one big map: featureName -> [classDisplayNames]
                // IMPORTANT: reduce with a seed returns Single<...> already; do NOT call toSingle() afterwards.
                .reduce(new LinkedHashMap<String, List<String>>(), (acc, entry) -> {
                    for (Map.Entry<String, List<String>> e : entry.entrySet()) {
                        acc.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).addAll(e.getValue());
                    }
                    return acc;
                });
    }

    /**
     * Build a map traitName -> list of race display names that grant it.
     *
     * @param raceIndexToDisplay map of race index to display name
     * @return Single that emits the map traitName -> race display names
     */
    public Single<Map<String, List<String>>> buildTraitToRacesMap(
            Map<String, String> raceIndexToDisplay
    ) {
        if (raceIndexToDisplay == null || raceIndexToDisplay.isEmpty()) {
            return Single.just(new LinkedHashMap<>());
        }

        return Flowable.fromIterable(raceIndexToDisplay.entrySet())
                // For each race entry (index -> display name), fetch its traits.
                .flatMap(en ->
                                api.listRaceTraits(en.getKey())
                                        .subscribeOn(Schedulers.io())
                                        .map(list -> toNameMultimapEntry(list, en.getValue()))
                                        .toFlowable(),
                        /* maxConcurrency */ 12
                )
                // Merge into traitName -> [raceDisplayNames]
                .reduce(new LinkedHashMap<String, List<String>>(), (acc, entry) -> {
                    for (Map.Entry<String, List<String>> e : entry.entrySet()) {
                        acc.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).addAll(e.getValue());
                    }
                    return acc;
                });
    }

    // Build a lookup keyed by "name|T/F" so we can update availability quickly.
    private static Map<String, Abilities> indexByNameAndFlag(List<Abilities> base) {
        Map<String, Abilities> map = new LinkedHashMap<>();
        if (base == null) return map;
        for (Abilities a : base) {
            if (a == null || a.getName() == null) continue;
            map.put(key(a.getName(), a.isTraitOrFeat()), a);
        }
        return map;
    }

    // Compose a stable key from name and whether this row represents a trait (true) or a feature (false).
    private static String key(String name, boolean traitOrFeat) {
        return (name == null ? "" : name.trim()) + "|" + (traitOrFeat ? "T" : "F");
    }

    // Take a ResourceListDto of refs and produce a map "ref.name -> [displayName]".
    // Used to build either feature->classes or trait->races relationships.
    private static Map<String, List<String>> toNameMultimapEntry(ResourceListDto list, String displayName) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        List<ResourceRefDto> refs =
                (list != null && list.results != null)
                        ? list.results
                        : java.util.Collections.<ResourceRefDto>emptyList();

        for (ResourceRefDto r : refs) {
            if (r == null || r.name == null) continue;
            out.computeIfAbsent(r.name.trim(), k -> new ArrayList<>()).add(displayName);
        }
        return out;
    }
}
