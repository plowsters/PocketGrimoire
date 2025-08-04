package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.Objects;

@Entity(tableName = PocketGrimoireDatabase.ITEMS_TABLE)
public class Items {
    @PrimaryKey (autoGenerate = true)
    private int itemID;
    String name;
    String category;

    @Override
    public String toString() {
        return "Items{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Items items = (Items) o;
        return itemID == items.itemID && Objects.equals(name, items.name) && Objects.equals(category, items.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, name, category);
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
