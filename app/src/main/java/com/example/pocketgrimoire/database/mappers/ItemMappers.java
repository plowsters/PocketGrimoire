package com.example.pocketgrimoire.database.mappers;

import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.remote.dto.ApiRef;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;

import java.util.HashMap;
import java.util.Locale;

/**
 * Mapping helpers for Items + small utilities to build category maps.
 */
public final class ItemMappers {

    private ItemMappers() {}

    /**
     * Build an Items entity from a ResourceRefDto and category name.
     * Also computes isEquippable using EquippableCategory.
     *
     * @param ref           equipment reference (index/name/url)
     * @param categoryName  display name for the equipment category (may be null)
     * @return Items row or null if ref/name is missing
     */
    public static Items fromEquipmentRef(ResourceRefDto ref, String categoryName) {
        if (ref == null || ref.name == null) return null;
        String name = normalize(ref.name);
        String cat  = normalize(categoryName);
        boolean equippable = EquippableCategory.isEquippable(cat);
        Items e = new Items();
        e.setName(name);
        e.setCategory(cat);
        e.setIsEquippable(equippable);
        return e;
    }

    /**
     * Add all equipment in a category response to the given index→categoryName map.
     * Keyed by equipment index (stable id from the API).
     *
     * @param acc index → category display name (mutated)
     * @param cat category response DTO
     */
    public static void putCategoryMemberships(HashMap<String,String> acc, EquipmentCategoryRequestDto cat) {
        if (acc == null || cat == null || cat.name == null || cat.equipment == null) return;
        for (ApiRef r : cat.equipment) {
            if (r == null || r.index == null) continue;
            acc.put(r.index, cat.name);
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
