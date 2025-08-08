package com.example.pocketgrimoire.database.seeding;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ClassRaceMaps {
    private ClassRaceMaps() {}

    public static Map<String, String> defaultClasses() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("barbarian","Barbarian");
        m.put("bard","Bard");
        m.put("cleric","Cleric");
        m.put("druid","Druid");
        m.put("fighter","Fighter");
        m.put("monk","Monk");
        m.put("paladin","Paladin");
        m.put("ranger","Ranger");
        m.put("rogue","Rogue");
        m.put("sorcerer","Sorcerer");
        m.put("warlock","Warlock");
        m.put("wizard","Wizard");
        return m;
    }

    public static Map<String, String> defaultRaces() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("dwarf","Dwarf");
        m.put("elf","Elf");
        m.put("halfling","Halfling");
        m.put("human","Human");
        m.put("dragonborn","Dragonborn");
        m.put("gnome","Gnome");
        m.put("half-elf","Half-Elf");
        m.put("half-orc","Half-Orc");
        m.put("tiefling","Tiefling");
        return m;
    }
}
