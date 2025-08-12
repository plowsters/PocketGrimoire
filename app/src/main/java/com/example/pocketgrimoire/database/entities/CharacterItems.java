package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

@Entity(
        tableName = PocketGrimoireDatabase.CHARACTER_ITEMS_TABLE,
        primaryKeys = {"characterID", "itemID"},
        foreignKeys = {
                @ForeignKey(
                        entity = CharacterSheet.class,
                        parentColumns = "characterID",
                        childColumns = "characterID",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Items.class,
                        parentColumns = "itemID",
                        childColumns = "itemID",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = { @Index("characterID"), @Index("itemID") }
)
public class CharacterItems {
    public int characterID;
    public int itemID;
    public int quantity;
    public boolean isEquipped;

    public CharacterItems() {}
    public CharacterItems(int characterID, int itemID, int quantity, boolean isEquipped) {
        this.characterID = characterID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.isEquipped = isEquipped;
    }
}
