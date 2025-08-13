package com.example.pocketgrimoire.database.mappers;

import androidx.core.util.Pair;

import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.remote.dto.ResourceListDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapping utilities for Abilities (features & traits).
 */
public final class AbilityMappers {
    private AbilityMappers() {}

    /**
     * Build an Abilities entity from raw parts.
     *
     * @param name              ability name; null/blank returns null
     * @param isFeature         true if this is a class Feature, false if a racial Trait
     * @param availableToClass  class display names (may be null)
     * @param availableToRace   race display names (may be null)
     * @return a new Abilities entity or null if the name is missing
     */
    public static Abilities fromParts(
            String name,
            boolean isFeature,
            List<String> availableToClass,
            List<String> availableToRace
    ) {
        String n = normalize(name);
        if (n == null || n.isEmpty()) return null;

        Abilities a = new Abilities();
        a.setName(n);
        a.setTraitOrFeat(isFeature);
        a.setAvailableToClass(dedupeNormalized(availableToClass));
        a.setAvailableToRace(dedupeNormalized(availableToRace));
        return a;
    }

    /**
     * NEW: Maps a list of API resource references to a list of basic Abilities entities.
     * @param listDto The DTO containing the list of references.
     * @param isFeature True if the list contains Features, false if it contains Traits.
     * @return A list of partially populated Abilities entities.
     */
    public static List<Abilities> fromResourceList(ResourceListDto listDto, boolean isFeature) {
        if (listDto == null || listDto.results == null) {
            return Collections.emptyList();
        }
        List<Abilities> abilities = new ArrayList<>();
        for (ResourceRefDto ref : listDto.results) {
            Abilities ability = fromParts(ref.name, isFeature, Collections.emptyList(), Collections.emptyList());
            if (ability != null) {
                abilities.add(ability);
            }
        }
        return abilities;
    }

    /** Normalize and remove duplicates while preserving encounter order. */
    private static List<String> dedupeNormalized(List<String> in) {
        if (in == null) return Collections.emptyList();
        Set<String> set = new LinkedHashSet<>();
        for (String s : in) {
            String n = normalize(s);
            if (n != null && !n.isEmpty()) set.add(n);
        }
        return new ArrayList<>(set);
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

    /**
     * Helper to create a mapping of a class/race name to its list of ability names
     * @param ownerName The name of the class or race (e.g., "Cleric", "Dwarf")
     * @param abilityListDto The DTO containing the list of abilities for that owner
     * @return A Pair where the first element is the owner name and the second is the list of ability names
     */
    public static Pair<String, List<String>> createOwnerToAbilityEntry(String ownerName, ResourceListDto abilityListDto) {
        List<String> abilityNames = new ArrayList<>();
        if (abilityListDto != null && abilityListDto.results != null) {
            for (ResourceRefDto ref : abilityListDto.results) {
                if (ref.name != null && !ref.name.isEmpty()) {
                    abilityNames.add(normalize(ref.name));
                }
            }
        }
        return new Pair<>(ownerName, abilityNames);
    }

    /**
     * Inverts the ownership map for efficient lookups
     * This is designed to be used with the RxJava .collect() operator. It takes a map like
     * {"Cleric" -> ["Feature A", "Feature B"]} and merges it into an accumulator map like
     * {"Feature A" -> ["Cleric"], "Feature B" -> ["Cleric"]}
     * @param accumulator The map being built (e.g., Map<AbilityName, List<OwnerName>>)
     * @param ownerToAbilitiesPair A Pair containing one owner and their list of abilities
     */
    public static void mergeIntoAvailabilityMap(Map<String, List<String>> accumulator, Pair<String, List<String>> ownerToAbilitiesPair) {
        String ownerName = ownerToAbilitiesPair.first;
        List<String> abilityNames = ownerToAbilitiesPair.second;

        if (ownerName == null || abilityNames == null) return;

        for (String abilityName : abilityNames) {
            accumulator.computeIfAbsent(abilityName, k -> new ArrayList<>()).add(ownerName);
        }
    }
}