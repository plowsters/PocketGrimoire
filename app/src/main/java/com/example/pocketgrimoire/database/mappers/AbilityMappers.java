package com.example.pocketgrimoire.database.mappers;

import com.example.pocketgrimoire.database.entities.Abilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mapping utilities for Abilities (features & traits).
 */
public final class AbilityMappers {
    private AbilityMappers() {}

    /**
     * Build an {@link Abilities} entity from raw parts.
     *
     * @param name              ability name; null/blank returns null
     * @param isTrait           true if this is a Race Trait, false if a class Feature
     * @param availableToClass  class display names (may be null)
     * @param availableToRace   race display names (may be null)
     * @return a new Abilities entity or null if the name is missing
     */
    public static Abilities fromParts(
            String name,
            boolean isTrait,
            List<String> availableToClass,
            List<String> availableToRace
    ) {
        String n = normalize(name);
        if (n == null || n.isEmpty()) return null;

        Abilities a = new Abilities();
        a.setName(n);
        // NEW: true = Trait, false = Feature
        a.setTraitOrFeat(isTrait);
        a.setAvailableToClass(normalizeDedupe(availableToClass));
        a.setAvailableToRace(normalizeDedupe(availableToRace));
        return a;
    }

    /** Normalize and remove duplicates while preserving encounter order. */
    private static List<String> normalizeDedupe(List<String> in) {
        List<String> out = new ArrayList<>();
        if (in == null || in.isEmpty()) return out;
        Set<String> seen = new HashSet<>();
        for (String s : in) {
            String n = normalize(s);
            if (n == null || n.isEmpty()) continue;
            if (seen.add(n)) out.add(n);
        }
        return out;
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