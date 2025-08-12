package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

@Entity(
        tableName = PocketGrimoireDatabase.CHARACTER_SPELLS_TABLE,
        primaryKeys = {"characterID", "spellID"},
        foreignKeys = {
                @ForeignKey(
                        entity = CharacterSheet.class,
                        parentColumns = "characterID",
                        childColumns = "characterID",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Spells.class,
                        parentColumns = "spellID",
                        childColumns = "spellID",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = { @Index("characterID"), @Index("spellID") }
)
public class CharacterSpells {
    public int characterID;
    public int spellID;
    public boolean isPrepared;

    public CharacterSpells() {}
    public CharacterSpells(int characterID, int spellID, boolean isPrepared) {
        this.characterID = characterID;
        this.spellID = spellID;
        this.isPrepared = isPrepared;
    }
}