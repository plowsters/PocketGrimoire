package com.example.pocketgrimoire.database.mappers;

import android.util.Pair;

import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ClassRaceMaps {

    // Helper to create a single entry for the map, e.g., ("Cleric", [list of feature names])
    public static Pair<String, List<String>> createClassToFeatureEntry(String className, ResourceListDto featureListDto) {
        List<String> featureNames = new ArrayList<>();
        if (featureListDto != null && featureListDto.results != null) {
            for (ResourceRefDto ref : featureListDto.results) {
                featureNames.add(ref.name);
            }
        }
        return new Pair<>(className, featureNames);
    }

    // A similar method for createRaceToTraitEntry
    public static Pair<String, List<String>> createRaceToTraitEntry(String raceName, ResourceListDto traitListDto) {
        List<String> traitNames = new ArrayList<>();
        if (traitListDto != null && traitListDto.results != null) {
            for (ResourceRefDto ref : traitListDto.results) {
                if (ref.name != null && !ref.name.isEmpty()) {
                    traitNames.add(normalize(ref.name));
                }
            }
        }
        return new Pair<>(raceName, traitNames);
    }

    // This is the key function for the .collect() operator. It inverts the map.
    // Instead of Map<ClassName, List<FeatureName>>, we create Map<FeatureName, List<ClassName>>
    public static void mergeIntoAvailabilityMap(Map<String, List<String>> accumulator, Pair<String, List<String>> classOrRaceEntry) {
        String ownerName = classOrRaceEntry.first; // e.g., "Cleric"
        List<String> abilityNames = classOrRaceEntry.second; // e.g., ["Bonus Proficiency", "Disciple of Life"]

        for (String abilityName : abilityNames) {
            accumulator.computeIfAbsent(abilityName, k -> new ArrayList<>()).add(ownerName);
        }
    }

    /**
     * Checks for null string, converts all whitespace (including \r\n) to a single space and trims the ends
     * @param s The string to be normalized
     * @return The normalized String (all leading and trailing whitespace gone, String separated by spaces)
     * */
    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.replaceAll("\\s+", " ");
    }
}