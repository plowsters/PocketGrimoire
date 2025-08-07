package com.example.pocketgrimoire.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Entity(tableName = PocketGrimoireDatabase.CHARACTER_SHEET_TABLE)
public class CharacterSheet {

    @PrimaryKey (autoGenerate = true)
    private int characterID;
    String characterName;
    String race;
    String clazz;
    String notes;
    String background;
    int level;
    int xpToLevel;
    int xp;
    int maxHP;
    int currentHP;
    int armorClass;
    int strength;
    int dexterity;
    int intelligence;
    int constitution;
    int charisma;
    int wisdom;
    int strSavingThrow;
    int dexSavingThrow;
    int intSavingThrow;
    int conSavingThrow;
    int charSavingThrow;
    int wisSavingThrow;
    List<String> proficiencies;
    List<String> traits;
    List<String> languages;
    HashMap<String,String> characteristics;

    @Override
    public String toString() {
        return "CharacterSheet{" +
                ", characterName='" + characterName + '\'' +
                ", race='" + race + '\'' +
                ", clazz='" + clazz + '\'' +
                ", notes='" + notes + '\'' +
                ", background='" + background + '\'' +
                ", level=" + level +
                ", xpToLevel=" + xpToLevel +
                ", xp=" + xp +
                ", maxHP=" + maxHP +
                ", currentHP=" + currentHP +
                ", armorClass=" + armorClass +
                ", strength=" + strength +
                ", dexterity=" + dexterity +
                ", intelligence=" + intelligence +
                ", constitution=" + constitution +
                ", charisma=" + charisma +
                ", wisdom=" + wisdom +
                ", strSavingThrow=" + strSavingThrow +
                ", dexSavingThrow=" + dexSavingThrow +
                ", intSavingThrow=" + intSavingThrow +
                ", conSavingThrow=" + conSavingThrow +
                ", charSavingThrow=" + charSavingThrow +
                ", wisSavingThrow=" + wisSavingThrow +
                ", proficiencies=" + proficiencies +
                ", traits=" + traits +
                ", languages=" + languages +
                ", characteristics=" + characteristics +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CharacterSheet that = (CharacterSheet) o;
        return characterID == that.characterID &&
                level == that.level &&
                xpToLevel == that.xpToLevel &&
                xp == that.xp &&
                maxHP == that.maxHP &&
                currentHP == that.currentHP &&
                armorClass == that.armorClass &&
                strength == that.strength &&
                dexterity == that.dexterity &&
                intelligence == that.intelligence &&
                constitution == that.constitution &&
                charisma == that.charisma &&
                wisdom == that.wisdom &&
                strSavingThrow == that.strSavingThrow &&
                dexSavingThrow == that.dexSavingThrow &&
                intSavingThrow == that.intSavingThrow &&
                conSavingThrow == that.conSavingThrow &&
                charSavingThrow == that.charSavingThrow &&
                wisSavingThrow == that.wisSavingThrow &&
                Objects.equals(characterName, that.characterName) &&
                Objects.equals(race, that.race) &&
                Objects.equals(clazz, that.clazz) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(background, that.background) &&
                Objects.equals(proficiencies, that.proficiencies) &&
                Objects.equals(traits, that.traits) &&
                Objects.equals(languages, that.languages) &&
                Objects.equals(characteristics, that.characteristics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterID, characterName, race, clazz, notes, background, level, xpToLevel, xp, maxHP, currentHP, armorClass, strength, dexterity, intelligence, constitution, charisma, wisdom, strSavingThrow, dexSavingThrow, intSavingThrow, conSavingThrow, charSavingThrow, wisSavingThrow, proficiencies, traits, languages, characteristics);
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXpToLevel() {
        return xpToLevel;
    }

    public void setXpToLevel(int xpToLevel) {
        this.xpToLevel = xpToLevel;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public void setArmorClass(int armorClass) {
        this.armorClass = armorClass;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getConstitution() {
        return constitution;
    }

    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getWisdom() {
        return wisdom;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getStrSavingThrow() {
        return strSavingThrow;
    }

    public void setStrSavingThrow(int strSavingThrow) {
        this.strSavingThrow = strSavingThrow;
    }

    public int getDexSavingThrow() {
        return dexSavingThrow;
    }

    public void setDexSavingThrow(int dexSavingThrow) {
        this.dexSavingThrow = dexSavingThrow;
    }

    public int getIntSavingThrow() {
        return intSavingThrow;
    }

    public void setIntSavingThrow(int intSavingThrow) {
        this.intSavingThrow = intSavingThrow;
    }

    public int getConSavingThrow() {
        return conSavingThrow;
    }

    public void setConSavingThrow(int conSavingThrow) {
        this.conSavingThrow = conSavingThrow;
    }

    public int getCharSavingThrow() {
        return charSavingThrow;
    }

    public void setCharSavingThrow(int charSavingThrow) {
        this.charSavingThrow = charSavingThrow;
    }

    public int getWisSavingThrow() {
        return wisSavingThrow;
    }

    public void setWisSavingThrow(int wisSavingThrow) {
        this.wisSavingThrow = wisSavingThrow;
    }
    public List<String> getProficiencies() {
        return proficiencies;
    }

    public void setProficiencies(List<String> proficiencies) {
        this.proficiencies = proficiencies;
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public HashMap<String, String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(HashMap<String, String> characteristics) {
        this.characteristics = characteristics;
    }
}
