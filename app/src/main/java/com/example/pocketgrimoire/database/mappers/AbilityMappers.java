package com.example.pocketgrimoire.database.mappers;

import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.entities.Abilities;

import java.util.*;

/**
 * Maps Traits and Features from the API to an Abilities entity
 *
 * First we get all available features and traits
 * /api/2014/features   -> traitOrFeat = false
 * /api/2014/traits     -> traitOrFeat = true
 *
 * Then we create lists to see which classes and races map to which features and abilities
 * /api/2014/classes/{c}/features -> add class to availableToClass for matching feature names
 * /api/2014/races/{r}/traits     -> add race to availableToRace for matching trait names
 *
 */
public final class AbilityMappers {

    private AbilityMappers() {}

    /**
     * Create a base FEATURE row (traitOrFeat=false) from a ref. Lists start empty.
     * */
    public static Abilities newFeature(ResourceRefDto ref) {
        if (ref == null) return null;
        Abilities e = new Abilities();
        e.setName(normalize(ref.name));
        e.setTraitOrFeat(false);
        e.setAvailableToClass(new ArrayList<>());
        e.setAvailableToRace(new ArrayList<>());
        return e;
    }

    /**
     * Create a base TRAIT row (traitOrFeat=true) from a ref. Lists start empty.
     * */
    public static Abilities newTrait(ResourceRefDto ref) {
        if (ref == null) return null;
        Abilities e = new Abilities();
        e.setName(normalize(ref.name));
        e.setTraitOrFeat(true);
        e.setAvailableToClass(new ArrayList<>());
        e.setAvailableToRace(new ArrayList<>());
        return e;
    }

    /**
     * Return a new list that merges availableToClass and availableToRace into the already existing
     * database entries
     *
     * @param existing  current values, likely to be null
     * @param additions values to merge, may be null
     * @return a new list containing unique, normalized values in stable order
     */
    public static List<String> mergeAvailability(List<String> existing, Collection<String> additions) {
        List<String> out = new ArrayList<>();

        if (existing != null) {
            // For all String values in the availableToClass/Race List
            for (String s : existing) {
                // normalize the string, check for null or already exists, add to the list
                String n = normalize(s);
                if (n != null && !n.isEmpty() && !out.contains(n)) {
                    out.add(n);
                }
            }
        }

        if (additions != null) {
            // basically the same as above, but adds new items to the list
            for (String s : additions) {
                String n = normalize(s);
                if (n != null && !n.isEmpty() && !out.contains(n)) {
                    out.add(n);
                }
            }
        }

        return out;
    }

    /**
     * Produce a merged list for availableToClass.
     * */
    public static List<String> mergeAvailableToClass(Abilities ability, Collection<String> classNames) {
        return mergeAvailability(ability != null ? ability.getAvailableToClass() : null, classNames);
    }

    /**
     * Produce a merged list for availableToRace.
     * */
    public static List<String> mergeAvailableToRace(Abilities ability, Collection<String> raceNames) {
        return mergeAvailability(ability != null ? ability.getAvailableToRace() : null, raceNames);
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