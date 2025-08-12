package com.example.pocketgrimoire.database.mappers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Class that defines which equipment categories can be equipped (weapons/armor/shields).
 */
public final class EquippableCategory {
    // Using hashset for constant time lookups, List was causing O(n^2) lookups
    private static final Set<String> EQUIPPABLE = new HashSet<>(Arrays.asList(
            "weapon",
            "melee-weapons",
            "ranged-weapons",
            "simple-weapons",
            "simple-melee-weapons",
            "simple-ranged-weapons",
            "martial-weapons",
            "martial-melee-weapons",
            "martial-ranged-weapons",
            "armor",
            "light-armor",
            "medium-armor",
            "heavy-armor",
            "shields"
    ));

    private EquippableCategory() {}

    /**
     * True if the String category corresponds to an equippable category.
     * */
    public static boolean isEquippable(String categoryNameOrIndex) {
        if (categoryNameOrIndex == null) return false;
        return EQUIPPABLE.contains(canon(categoryNameOrIndex));
    }

    /**
     * Convert "Simple Weapons", "simple_weapons", or "Simple   Weapons" → "simple-weapons".
     * */
    static String canon(String s) {
        String t = s.trim().toLowerCase(Locale.ROOT);
        t = t.replaceAll("[\\s_]+", "-");   // spaces/underscores → hyphen
        t = t.replaceAll("[^a-z0-9-]", ""); // drop punctuation except hyphen
        return t;
    }
}
