package com.example.pocketgrimoire.database.mappers;

import com.example.pocketgrimoire.database.remote.dto.SpellRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ApiRef;
import com.example.pocketgrimoire.database.entities.Spells;

import java.util.*;

/**
 * Mapping utilities for Spells.
 * We map SpellRequestDto to the Spells entity with:
 * - name (String)
 * - level (int)              (null -> 0 fallback)
 * - school (String)          (from school.name)
 * - availableToClass (List)  (unique, stable order)
 *
 */
public final class SpellMappers {

    private SpellMappers() {}

    /**
     * Map a single spell DTO to a Spells entity.
     *
     * @param dto the spell DTO obtained from the spells request endpoint; may be null
     * @return a new Spells entity populated from dto, or null if dto is null
     */
    public static Spells fromDetail(SpellRequestDto dto) {
        if (dto == null) return null;
        Spells e = new Spells();

        e.setName(normalize(dto.name));
        // API should provide it, but fall back to 0 if missing
        e.setLevel(dto.level != null ? dto.level : 0);

        String schoolName = (dto.school != null ? dto.school.name : null);
        e.setSchool(normalize(schoolName));

        // Extract class names, dedupe while preserving encounter order
        e.setAvailableToClass(extractUniqueNames(dto.classes));
        return e;
    }

    /**
     * Convert a list of ApiRef (for example, class references) into a list of normalized names,
     * removing duplicates while preserving first-seen order.
     *
     * @param refs list of API references (for example, dto.classes); may be null
     * @return a new list of unique, normalized names in stable order (never null)
     */
    public static List<String> extractUniqueNames(List<ApiRef> refs) {
        List<String> out = new ArrayList<>();
        if (refs == null || refs.isEmpty()) return out;

        // for every class passed in from the API
        for (ApiRef r : refs) {
            if (r == null) continue;
            String name = normalize(r.name);
            // check for null before and after normalization
            if (name == null || name.isEmpty()) continue;

            // Preserves order of classes
            if (!out.contains(name)) {
                out.add(name);
            }
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