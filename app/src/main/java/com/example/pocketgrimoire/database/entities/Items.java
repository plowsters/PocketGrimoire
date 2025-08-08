package com.example.pocketgrimoire.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

/** Items table: (itemID PK, name UNIQUE, category). */
@Entity(
        tableName = PocketGrimoireDatabase.ITEMS_TABLE,
        indices = { @Index(value = {"name"}, unique = true) }
)
public class Items {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemID")
    private int itemID;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "category")
    private String category;

    public Items(@NonNull String name, String category) {
        this.name = name;
        this.category = category;
    }

    public int getItemID() { return itemID; }
    public void setItemID(int itemID) { this.itemID = itemID; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
