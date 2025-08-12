package com.example.pocketgrimoire.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.ArrayList;
import java.util.List;

/** Abilities table with unique (name, traitOrFeat). */
@Entity(tableName = PocketGrimoireDatabase.ABILITIES_TABLE)
public class Abilities {
    @PrimaryKey(autoGenerate = true)
    private int abilityID;

    @NonNull
    private String name;

    private boolean traitOrFeat;

    private List<String> availableToClass;

    private List<String> availableToRace;

    public Abilities() {
        // No argument constructor for RoomDB initialization
    }
    @Ignore
    public Abilities(@NonNull String name, boolean traitOrFeat) {
        this.name = name;
        this.traitOrFeat = traitOrFeat;
        this.availableToClass = new ArrayList<>();
        this.availableToRace = new ArrayList<>();
    }

    public int getAbilityID() { return abilityID; }
    public void setAbilityID(int abilityID) { this.abilityID = abilityID; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public boolean isTraitOrFeat() { return traitOrFeat; }
    public void setTraitOrFeat(boolean traitOrFeat) { this.traitOrFeat = traitOrFeat; }

    public List<String> getAvailableToClass() { return availableToClass; }
    public void setAvailableToClass(List<String> availableToClass) { this.availableToClass = availableToClass; }

    public List<String> getAvailableToRace() { return availableToRace; }
    public void setAvailableToRace(List<String> availableToRace) { this.availableToRace = availableToRace; }
}