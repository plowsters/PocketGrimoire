package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

@Entity(
        tableName = PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE,
        primaryKeys = {"characterID", "abilityID"},
        foreignKeys = {
                @ForeignKey(
                        entity = CharacterSheet.class,
                        parentColumns = "characterID",
                        childColumns = "characterID",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Abilities.class,
                        parentColumns = "abilityID",
                        childColumns = "abilityID",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = { @Index("characterID"), @Index("abilityID") }
)
public class CharacterAbilities {
    public int characterID;
    public int abilityID;

    public CharacterAbilities() {}
    public CharacterAbilities(int characterID, int abilityID) {
        this.characterID = characterID;
        this.abilityID = abilityID;
    }
}