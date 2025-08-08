package com.example.pocketgrimoire.database.mappers;

import com.example.pocketgrimoire.database.remote.dto.SpellRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ApiRef;
import com.example.pocketgrimoire.database.entities.Spells;

import java.util.*;

/**
 * Mapping utilities for Spells.
 * We map SpellRequestDto (detail) to the Spells entity with:
 * - name (String)
 * - level (int)              (null -> 0 fallback)
 * - school (String)          (from school.name)
 * - availableToClass (List)  (unique, stable order)
 *
 * NOTE: We dedupe by "name" at the repository/DAO layer (update-by-name, else insert).
 */
public final class SpellMappers {

    private SpellMappers() {}

    public static Spells fromDetail(SpellRequestDto dto) {
        if (dto == null) return null;
        Spells e = new Spells();

        e.setName(normalize(dto.name));
        e.setLevel(dto.level != null ? dto.level : 0);

        String schoolName = (dto.school != null ? dto.school.name : null);
        e.setSchool(normalize(schoolName));

        // Extract class names, dedupe while preserving encounter order
        e.setAvailableToClass(extractUniqueNames(dto.classes));
        return e;
    }

    /** Turn a list of ApiRef into a deduped, normalized list of names (preserve order). */
    public static List<String> extractUniqueNames(List<ApiRef> refs) {
        if (refs == null || refs.isEmpty()) return new ArrayList<>();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (ApiRef r : refs) {
            if (r == null) continue;
            String name = normalize(r.name);
            if (name != null && !name.isEmpty()) set.add(name);
        }
        return new ArrayList<>(set);
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.replaceAll("\\s+", " ");
    }
}