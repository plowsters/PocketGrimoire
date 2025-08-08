package com.example.pocketgrimoire.database.mappers;

import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.dto.EquipmentCategoryRequestDto;
import com.example.pocketgrimoire.database.remote.dto.ApiRef;
import com.example.pocketgrimoire.database.entities.Items;

import java.util.*;

/**
 * Maps DTOs from API calls to the RoomDB Items Object
 * NOTE: We dedupe by "name" at the repository/DAO layer (update-by-name, else insert)
 */
public final class ItemMappers {

    private ItemMappers() {}

    /**
     * Map a single equipment reference + its category name to an Items entity object
     * The DB's primary key (itemID) is auto-incremented
     * @param ref the default Dto structure for D&D 5e API
     * @param category the equipment category of the item
     * @return Items object that can now be inserted into the DB
     */
    public static Items fromEquipmentRef(ResourceRefDto ref, String category) {
        if (ref == null) return null;
        Items e = new Items();
        e.setName(normalize(ref.name));
        e.setCategory(normalize(category));
        return e;
    }

    /**
     * API lookup: equipment index -> category NAME.
     * This lets you take results from /equipment and fill "category" without calling each item detail.
     *
     * @param categories List of EquipmentCategoryRequestDto objects from /api/2014/equipment-categories/{index}
     * @return A HashMap with key = API index of the equipment reference, value = normalized String category for RoomDB entity
     */
    public static Map<String, String> buildIndexToCategoryMap(List<EquipmentCategoryRequestDto> categories) {
        Map<String, String> map = new HashMap<>();
        if (categories == null) return map;

        for (EquipmentCategoryRequestDto cat : categories) {
            String catName = normalize(cat.name);
            if (cat.equipment == null) continue;

            for (ApiRef eq : cat.equipment) {
                if (eq == null || eq.index == null) continue;
                map.put(eq.index, catName);
            }
        }
        return map;
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
