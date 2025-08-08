package com.example.pocketgrimoire.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.pocketgrimoire.database.typeConverters.Converters;
import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.ArrayList;
import java.util.List;

/** Spells table: (spellID PK, name UNIQUE, level, school, availableToClass). */
@Entity(
        tableName = PocketGrimoireDatabase.SPELLS_TABLE,
        indices = { @Index(value = {"name"}, unique = true) }
)
public class Spells {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "spellID")
    private int spellID;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "level")
    private int level;

    @ColumnInfo(name = "school")
    private String school;

    @ColumnInfo(name = "availableToClass")
    private List<String> availableToClass;

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
}
