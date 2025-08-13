package com.example.pocketgrimoire.network.loaders;

import androidx.core.util.Pair;

import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.mappers.AbilityMappers;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.seeding.ClassRaceMaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Loads and fully populates Abilities (Features + Traits) including their
 * class and race availability lists.
 */
public final class AbilitiesNetworkLoader {
    private final DndApiService api;

    public AbilitiesNetworkLoader(DndApiService api) {
        this.api = api;
    }

    /**
     * Orchestrates the fetching and merging of all ability data sequentially to respect API rate limits.
     * 1. Fetches all base features and traits.
     * 2. THEN, it builds a map of availability for classes.
     * 3. FINALLY, it builds a map for races and zips the results together.
     */
    public Single<List<Abilities>> fetchAll() {
        return getBaseAbilitiesMap()
                .flatMap(baseAbilities ->
                        buildFeatureToClassMap()
                                .flatMap(featureMap ->
                                        buildTraitToRaceMap()
                                                .map(traitMap -> {
                                                    // This is the final "stitching together" step, executed after all data is fetched.
                                                    for (Abilities ability : baseAbilities.values()) {
                                                        if (ability.isTraitOrFeat()) { // It's a Feature
                                                            List<String> classes = featureMap.get(ability.getName());
                                                            if (classes != null) {
                                                                ability.setAvailableToClass(classes);
                                                            }
                                                        } else { // It's a Trait
                                                            List<String> races = traitMap.get(ability.getName());
                                                            if (races != null) {
                                                                ability.setAvailableToRace(races);
                                                            }
                                                        }
                                                    }
                                                    return new ArrayList<>(baseAbilities.values());
                                                })
                                )
                );
    }

    /**
     * Fetches all features and traits from the API and maps them to a base
     * Abilities entity, stored in a Map for quick lookups.
     */
    private Single<Map<String, Abilities>> getBaseAbilitiesMap() {
        Single<List<Abilities>> features = api.listFeatures()
                .map(dto -> AbilityMappers.fromResourceList(dto, true)); // isFeature = true

        Single<List<Abilities>> traits = api.listTraits()
                .map(dto -> AbilityMappers.fromResourceList(dto, false)); // isFeature = false

        // Combine both lists and convert to a Map<Name, Ability>
        return Single.zip(features, traits, (featureList, traitList) -> {
            Map<String, Abilities> map = new LinkedHashMap<>();
            for (Abilities f : featureList) { map.put(f.getName(), f); }
            for (Abilities t : traitList) { map.putIfAbsent(t.getName(), t); }
            return map;
        });
    }

    /**
     * Builds a Map where Key = Feature Name and Value = List of classes that have it.
     */
    private Single<Map<String, List<String>>> buildFeatureToClassMap() {
        // Use the hardcoded list of class keys from your existing ClassRaceMaps
        return Flowable.fromIterable(ClassRaceMaps.defaultClasses().keySet())
                .flatMap(classIndex ->
                                // For each class, fetch its features. Bounded concurrency is kind to the API.
                                api.listClassFeatures(classIndex).toFlowable()
                                        .map(featureListDto -> AbilityMappers.createOwnerToAbilityEntry(
                                                ClassRaceMaps.defaultClasses().get(classIndex), // Use the proper name
                                                featureListDto)
                                        ),
                        false, 4 // Bounded Concurrency
                )
                .collect(HashMap<String, List<String>>::new, AbilityMappers::mergeIntoAvailabilityMap);
    }

    /**
     * Builds a Map where Key = Trait Name and Value = List of races that have it.
     */
    private Single<Map<String, List<String>>> buildTraitToRaceMap() {
        // Follows the exact same pattern as above, but for races and traits.
        return Flowable.fromIterable(ClassRaceMaps.defaultRaces().keySet())
                .flatMap(raceIndex ->
                                api.listRaceTraits(raceIndex).toFlowable()
                                        .map(traitListDto -> AbilityMappers.createOwnerToAbilityEntry(
                                                ClassRaceMaps.defaultRaces().get(raceIndex), // Use the proper name
                                                traitListDto)
                                        ),
                        false, 4 // Bounded Concurrency
                )
                .collect(HashMap<String, List<String>>::new, AbilityMappers::mergeIntoAvailabilityMap);
    }
}
