package com.example.pocketgrimoire.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

/** Items table: (itemID PK, name UNIQUE, category). */
@Entity(tableName = PocketGrimoireDatabase.ITEMS_TABLE)
public class Items {
    @PrimaryKey(autoGenerate = true)
    private int itemID;

    @NonNull
    private String name;
    private String category;

    private boolean isEquippable;

    public Items() {
      // no argument constructor for RoomDB initialization
    }

    @Ignore
    public Items(@NonNull String name, String category) {
        this.name = name;
        this.category = category;
        this.isEquippable = false;
    }

    public int getItemID() { return itemID; }
    public void setItemID(int itemID) { this.itemID = itemID; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isEquippable() {
        return isEquippable;
    }
    public void setIsEquippable(boolean equippable) {
        isEquippable = equippable;
    }
}
