package com.example.pocketgrimoire.database.typeConverters;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A class containing TypeConverter methods for Lists and HashMaps in the RoomDB.
 * Converts complex data types (List, HashMap) into a Json object and stored as a
 * String primitive to be compatible with RoomDB, and functions to reverse this as well.
 */
public class Converters {

    // Create a Gson object to serialize Json objects as Strings
    private static Gson gson = new Gson();

    /**
     * Converts a List of Strings into a single JSON String for database storage.
     *
     * @param list The List of Strings to be converted.
     * @return A JSON String representation of the list. Returns null if the list is null.
     */
    @TypeConverter
    public static String listToJsonString(List<String> list) {
        return gson.toJson(list);
    }

    /**
     * Converts a JSON String from the database back into a List of Strings.
     *
     * @param data The JSON String retrieved from the database.
     * @return A List of Strings. Returns an empty list if the Json String is null.
     */
    @TypeConverter
    public static List<String> jsonStringToList(String data) {
        if (data == null) {
            // Collections is the parent class that contains interfaces for List, HashMap, etc.
            return Collections.emptyList();
        }
        // TypeToken class is how Gson knows what format to deserialize the Json String into
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    /**
     * Converts a HashMap of String keys and String values into a single JSON String
     *
     * @param map The HashMap to be converted.
     * @return A JSON String representation of the map. Returns null if the map is null.
     */
    @TypeConverter
    public static String hashMapToJsonString(HashMap<String, String> map) {
        return gson.toJson(map);
    }

    /**
     * Converts a JSON String from the database back into a HashMap.
     *
     * @param data The JSON String retrieved from the database.
     * @return A HashMap with String keys and String values. Returns an empty map if the data is null.
     */
    @TypeConverter
    public static HashMap<String, String> jsonStringToHashMap(String data) {
        if (data == null) {
            return new HashMap<>();
        }
        // Uses TypeToken again here to deserialize into a HashMap with key=String, value=String
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        return gson.fromJson(data, mapType);
    }
}