package com.example.pocketgrimoire.database.entities;

import android.text.TextUtils;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Entity(tableName = PocketGrimoireDatabase.CHARACTER_SHEET_TABLE,
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userID",
                childColumns = "userID",
                onDelete = ForeignKey.CASCADE),
        indices = @Index(value = {"userID"}))
public class CharacterSheet implements Serializable {

    @PrimaryKey (autoGenerate = true)
    private int characterID;
    private int userID;
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
    List<String> proficiencies; //requires API call and creation of a pseudolist as Lists cannot be added into databases - low priority
    List<String> traits; //remove
    String languages;
    HashMap<String,String> characteristics; //cannot add HashMap into database, will require to make a pseudo HashMap - low priority
    String characterAlignment;
    String gender;
    String eyeColor;
    String hairColor;
    String skinColor;
    int age;
    int height;
    int weight;

    @Override
    public String toString() {
        return "CharacterSheet{" +
                ", userID='" + userID + '\'' +
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
                ", proficiencies=" + proficiencies +
                ", traits=" + traits +
                ", languages=" + languages +
                ", characteristics=" + characteristics +
                ", characterAlignment=" + characterAlignment +
                ", gender=" + gender +
                ", hairColor=" + hairColor +
                ", eyeColor=" + eyeColor +
                ", skinColor=" + skinColor +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                '}';
    }

    //TODO: Re-add Objects.equals(proficiencies, that.proficiencies) && Objects.equals(traits, that.traits) && Objects.equals(languages, that.languages) && Objects.equals(characteristics, that.characteristics);
//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        CharacterSheet that = (CharacterSheet) o;
//        return  userID == that.userID &&
//                characterID == that.characterID &&
//                level == that.level &&
//                xpToLevel == that.xpToLevel &&
//                xp == that.xp &&
//                maxHP == that.maxHP &&
//                currentHP == that.currentHP &&
//                armorClass == that.armorClass &&
//                strength == that.strength &&
//                dexterity == that.dexterity &&
//                intelligence == that.intelligence &&
//                constitution == that.constitution &&
//                charisma == that.charisma &&
//                wisdom == that.wisdom &&
//                Objects.equals(characterName, that.characterName) &&
//                Objects.equals(race, that.race) &&
//                Objects.equals(clazz, that.clazz) &&
//                Objects.equals(notes, that.notes) &&
//                Objects.equals(background, that.background) &&
//                Objects.equals(proficiencies, that.proficiencies) &&
//                Objects.equals(traits, that.traits) &&
//                Objects.equals(languages, that.languages) &&
//                Objects.equals(characteristics, that.characteristics);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userID, characterID, characterName, race, clazz, notes, background, level, xpToLevel, xp, maxHP, currentHP, armorClass, strength, dexterity, intelligence, constitution, charisma, wisdom, strSavingThrow, dexSavingThrow, intSavingThrow, conSavingThrow, charSavingThrow, wisSavingThrow, proficiencies, traits, languages, characteristics);
//    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CharacterSheet that = (CharacterSheet) o;
            return characterID == that.characterID &&
                userID == that.userID &&
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
                    age == that.age &&
                    height == that.height &&
                    weight == that.weight &&
                    Objects.equals(characterName, that.characterName) &&
                    Objects.equals(race, that.race) &&
                    Objects.equals(clazz, that.clazz) &&
                    Objects.equals(notes, that.notes) &&
                    Objects.equals(background, that.background) &&
                    Objects.equals(proficiencies, that.proficiencies) &&
                    Objects.equals(traits, that.traits) &&
                    Objects.equals(languages, that.languages) &&
                    Objects.equals(characteristics, that.characteristics) &&
                    Objects.equals(characterAlignment, that.characterAlignment) &&
                    Objects.equals(gender, that.gender) &&
                    Objects.equals(eyeColor, that.eyeColor) &&
                    Objects.equals(hairColor, that.hairColor) &&
                    Objects.equals(skinColor, that.skinColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterID, userID, characterName, race, clazz, notes, background, level, xpToLevel, xp, maxHP, currentHP, armorClass, strength, dexterity, intelligence, constitution, charisma, wisdom, proficiencies, traits, languages, characteristics, characterAlignment, gender, eyeColor, hairColor, skinColor, age, height, weight);
    }

    public String getCharacterAlignment() {
        return characterAlignment;
    }

    public void setCharacterAlignment(String characterAlignment) {
        this.characterAlignment = characterAlignment;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public List<String> getLanguagesArray() {
        return Arrays.asList(languages.split(","));
    }

    public void setLanguagesArray(List<String> languages) {
        this.languages = TextUtils.join(",",languages);
    }
    public HashMap<String, String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(HashMap<String, String> characteristics) {
        this.characteristics = characteristics;
    }
}
