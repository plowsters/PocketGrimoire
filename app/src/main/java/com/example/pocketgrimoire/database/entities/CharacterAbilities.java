package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

@Entity(tableName = PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE)
public class CharacterAbilities {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int characterId;  // FK → CharacterSheet.characterID
    private int abilityId;    // FK → Abilities.id
    private boolean enabled;  // toggle for campaign

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCharacterId() { return characterId; }
    public void setCharacterId(int characterId) { this.characterId = characterId; }

    public int getAbilityId() { return abilityId; }
    public void setAbilityId(int abilityId) { this.abilityId = abilityId; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}