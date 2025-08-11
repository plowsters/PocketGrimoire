package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a spell that can be enabled or disabled in a campaign.
 * This will be used by CharacterSpells later.
 */
@Entity(tableName = "spells")
public class Spells {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;        // Spell name
    private String apiIndex;    // D&D API unique identifier
    private int level;          // Level (0 = Cantrip, 1..9)
    private String school;      // School of magic (e.g., "Evocation")
    private boolean enabled;    // Whether the spell is active in the campaign

    public Spells(String name, String apiIndex, int level, String school, boolean enabled) {
        this.name = name;
        this.apiIndex = apiIndex;
        this.level = level;
        this.school = school;
        this.enabled = enabled;
    }

    // ----- Getters and Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getApiIndex() { return apiIndex; }
    public void setApiIndex(String apiIndex) { this.apiIndex = apiIndex; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}