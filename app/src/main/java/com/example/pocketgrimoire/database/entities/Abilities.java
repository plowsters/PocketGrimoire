package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents an ability that can be enabled or disabled in a campaign.
 * This will be used by CharacterAbilities later.
 */
@Entity(tableName = "abilities")
public class Abilities {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;         // Ability name
    private String apiIndex;     // D&D API index (unique string)
    private String description;  // Optional short description (or blank if missing)
    private boolean enabled;     // Whether this ability is active for the campaign
    private boolean traitOrFeat; // false = Feature, true = Trait

    public Abilities(String name, String apiIndex, String description, boolean enabled, boolean traitOrFeat) {
        this.name = name;
        this.apiIndex = apiIndex;
        this.description = description;
        this.enabled = enabled;
        this.traitOrFeat = traitOrFeat;
    }

    // ----- Getters and Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getApiIndex() { return apiIndex; }
    public void setApiIndex(String apiIndex) { this.apiIndex = apiIndex; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isTraitOrFeat() { return traitOrFeat; }
    public void setTraitOrFeat(boolean traitOrFeat) { this.traitOrFeat = traitOrFeat; }
}