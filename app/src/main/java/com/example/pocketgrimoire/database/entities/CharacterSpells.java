package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

@Entity(tableName = PocketGrimoireDatabase.CHARACTER_SPELLS_TABLE)
public class CharacterSpells {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int characterId;   // FK → CharacterSheet.characterID
    private int spellId;       // FK → Spells.id   (or apiIndex if you prefer)
    private boolean prepared;  // for spellcasters

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCharacterId() { return characterId; }
    public void setCharacterId(int characterId) { this.characterId = characterId; }

    public int getSpellId() { return spellId; }
    public void setSpellId(int spellId) { this.spellId = spellId; }

    public boolean isPrepared() { return prepared; }
    public void setPrepared(boolean prepared) { this.prepared = prepared; }
}