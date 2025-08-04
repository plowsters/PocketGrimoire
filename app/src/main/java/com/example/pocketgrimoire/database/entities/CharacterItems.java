package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.Objects;

@Entity(tableName = PocketGrimoireDatabase.CHARACTER_ITEMS_TABLE,
        primaryKeys = {"characterID", "itemID"},
        foreignKeys = {@ForeignKey(
                entity = CharacterSheet.class,
                parentColumns = "characterID",
                childColumns = "characterID",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                        entity = Items.class,
                        parentColumns = "itemID",
                        childColumns = "itemID",
                        onDelete = ForeignKey.CASCADE)},
        indices = {
                @Index(value = {"characterID"}),
                @Index(value = {"itemID"})
        })
public class CharacterItems {
    private int characterID;
    private int itemID;
    private int quantity;
    private int isEquipped;

    @Override
    public String toString() {
        return "CharacterItems{" +
                "characterID=" + characterID +
                ", itemID=" + itemID +
                ", quantity=" + quantity +
                ", isEquipped=" + isEquipped +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CharacterItems that = (CharacterItems) o;
        return characterID == that.characterID && itemID == that.itemID && quantity == that.quantity && isEquipped == that.isEquipped;
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterID, itemID, quantity, isEquipped);
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIsEquipped() {
        return isEquipped;
    }

    public void setIsEquipped(int isEquipped) {
        this.isEquipped = isEquipped;
    }
}
