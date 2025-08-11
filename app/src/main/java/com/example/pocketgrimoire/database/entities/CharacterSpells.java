package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

/**
 * Junction table linking a Character to a Spell.
 * 'prepared' indicates if the spell is prepared for use.
 */
@Entity(tableName = PocketGrimoireDatabase.CHARACTER_SPELLS_TABLE)
public class CharacterSpells {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int characterID;  // FK -> CharacterSheet.characterID
    private int spellID;      // FK -> Spells.id
    private boolean prepared; // true if prepared

    // ----- Getters/Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCharacterID() { return characterID; }
    public void setCharacterID(int characterID) { this.characterID = characterID; }

    public int getSpellID() { return spellID; }
    public void setSpellID(int spellID) { this.spellID = spellID; }

    public boolean isPrepared() { return prepared; }
    public void setPrepared(boolean prepared) { this.prepared = prepared; }
}
CharacterAbilities.java
        java
Copy
        Edit
package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

/**
 * Junction table linking a Character to an Ability (Trait or Feature).
 * 'enabled' lets the user toggle abilities for a campaign.
 */
@Entity(tableName = PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE)
public class CharacterAbilities {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int characterID;  // FK -> CharacterSheet.characterID
    private int abilityID;    // FK -> Abilities.id
    private boolean enabled;  // true if active for this character

    // ----- Getters/Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCharacterID() { return characterID; }
    public void setCharacterID(int characterID) { this.characterID = characterID; }

    public int getAbilityID() { return abilityID; }
    public void setAbilityID(int abilityID) { this.abilityID = abilityID; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}