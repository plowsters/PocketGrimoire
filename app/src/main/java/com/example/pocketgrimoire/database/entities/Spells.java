package com.example.pocketgrimoire.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Spells table: (spellID PK, name UNIQUE, level, school, availableToClass). */
@Entity(tableName = PocketGrimoireDatabase.SPELLS_TABLE)
public class Spells {
    @PrimaryKey(autoGenerate = true)
    private int spellID;

    @NonNull
    private String name;

    private int level;

    private String school;

    private List<String> availableToClass;

    public Spells() {
        // No argument constructor for RoomDB initialization
    }
    @Ignore
    public Spells(@NonNull String name, int level, String school, List<String> availableToClass) {
        this.name = name;
        this.level = level;
        this.school = school;
        this.availableToClass = (availableToClass != null) ? availableToClass : new ArrayList<>();
    }

    public int getSpellID() { return spellID; }
    public void setSpellID(int spellID) { this.spellID = spellID; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public List<String> getAvailableToClass() { return availableToClass; }
    public void setAvailableToClass(List<String> availableToClass) { this.availableToClass = availableToClass; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Spells)) return false;
        Spells spells = (Spells) o;
        return getSpellID() == spells.getSpellID() && getLevel() == spells.getLevel() && Objects.equals(getName(), spells.getName()) && Objects.equals(getSchool(), spells.getSchool()) && Objects.equals(getAvailableToClass(), spells.getAvailableToClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpellID(), getName(), getLevel(), getSchool(), getAvailableToClass());
    }
}
