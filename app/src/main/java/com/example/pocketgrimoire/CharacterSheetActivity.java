package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.databinding.ActivityCharacterSheetBinding;
import com.example.pocketgrimoire.fragments.AccountDialogFragment;
import com.example.pocketgrimoire.fragments.DiceRollerFragment;

import java.io.Serializable;

public class CharacterSheetActivity extends AppCompatActivity {

    private static final String CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY = "CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY";
    private CharacterSheet character;
    private ActivityCharacterSheetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterSheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        character = (CharacterSheet) getIntent().getSerializableExtra(CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY);

        createBindings();

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_account) {
                AccountDialogFragment dialog = new AccountDialogFragment();
                dialog.show(getSupportFragmentManager(), "AccountDialogFragment");
                return true;
            } else if (itemId == R.id.navigation_dice) {
                DiceRollerFragment diceRollerFragment = new DiceRollerFragment();
                diceRollerFragment.show(getSupportFragmentManager(), diceRollerFragment.getTag());
                return true;
            }
            return false;
        });
    }

    private void createBindings() {
        //display character data top card - getTopCard()
        binding.characterNameTextView.setText(character.getCharacterName());
        binding.currentHPTextView.setText(String.valueOf(character.getCurrentHP()));
        binding.raceTextView.setText(character.getRace());
        binding.classTextView.setText(character.getClazz());
        binding.genderTextView.setText(character.getGender());
        calculateArmorClass(); //calculates and display armor class
        int currLevel = showCurrLevel(); //displays current level
        calculateMaxXP(currLevel); //displays current max xp depending on level
        currentCharXP(); //displays current character xp
        calculateCurrHP(); //displays current character hp
        //attributes - getAttributes()
        binding.strValueTextView.setText(String.valueOf(character.getStrength()));
        binding.dexValueTextView.setText(String.valueOf(character.getDexterity()));
        binding.intValueTextView.setText(String.valueOf(character.getIntelligence()));
        binding.chaValueTextView.setText(String.valueOf(character.getCharisma()));
        binding.conValueTextView.setText(String.valueOf(character.getConstitution()));
        binding.wisValueTextView.setText(String.valueOf(character.getWisdom()));
        binding.wisValueTextView.setText(String.valueOf(character.getWisdom()));
        //stat modifiers
        getStatModifiers();
        //characteristics - getCharacteristics()
        binding.backgroundTextView.setText(character.getBackground());
        binding.eyeColorTextView.setText(character.getEyeColor());
        binding.hairColorTextView.setText(character.getHairColor());
        binding.skinColorTextView.setText(character.getSkinColor());
        binding.ageTextView.setText(String.valueOf(character.getAge()));
        binding.heightTextView.setText(String.valueOf(character.getHeight()));
        binding.weightTextView.setText(String.valueOf(character.getWeight()));
        binding.charAlignTextView.setText(character.getCharacterAlignment());
        //notes
        binding.notesTextView.setText(character.getNotes());
    }

    /**
     * calculateArmorClass calculates the armor class value
     * The base armor class value is 10
     * To calculate armor class value, add base value to the character's dexterity value
     */
    private void calculateArmorClass() {
        int currArmor = 0;
        int armorClassValue = 10 + character.getDexterity() + currArmor;
        character.setArmorClass(armorClassValue);
        binding.armorClassValueTextView.setText(String.valueOf(character.getArmorClass()));
    }

    /**
     * Displays current level
     * Every character will always start with level 1
     * TODO: if there is time, set level to input and change HP and XP accordingly
     * @return
     */
    private int showCurrLevel() {
        character.setLevel(1);
        int currLevel = character.getLevel();
        binding.currLevelEditText.setText(String.valueOf(currLevel));
        return currLevel;
    }

    /**
     * sets and displays current character XP
     * For now it will always show 0
     */
    private void currentCharXP() {
        int currCharXP = 0;
        character.setXpToLevel(0);
        binding.xpToLevelTextView.setText(String.valueOf(character.getXpToLevel()));
    }

    /**
     * calculateMaxXP calculates max XP for each class depending on their level
     * Starting max xp is 300
     * The calculation is based on 300 * current character level
     * @param currLevel
     */
    private void calculateMaxXP(int currLevel) {
        int maxXP = 300 * currLevel;
        binding.maxXPTextView.setText(String.valueOf(maxXP));
    }

    /**
     * findHitDie assigns hitDie for each class
     */
    private int findHitDie() {
        int hitDie = 0;
        String clazz = character.getClazz();
        switch (clazz) {
            case "barbarian":
                hitDie = 12;
                break;
            case "barb":
            case "cleric":
            case "druid":
            case "monk":
            case "rogue":
            case "warlock":
                hitDie = 8;
                break;
            case "fighter":
            case "paladin":
            case "ranger":
                hitDie =10;
                break;
            case "sorcerer":
            case "wizard":
                hitDie = 6;
                break;
        }
        return hitDie;
    }

    /**
     * calculateCurrHP calculates max HP for the character per level (hopefully)
     */
    private void calculateCurrHP() {

        System.out.println("current level: " + character.getLevel());
        int currHP = character.getCurrentHP();
        int maxHP = character.getMaxHP();
        int currLevel = character.getLevel();
        int hitDie = findHitDie();

        //level 1
        if (currLevel == 1) {
            int con = character.getConstitution();
            currHP = hitDie + con;
            character.setCurrentHP(currHP);
            character.setMaxHP(currHP);
            binding.currentHPTextView.setText(String.valueOf(currHP));
            binding.maxHPTextView.setText(String.valueOf(currHP));
        } else {
            int sum = 0;
            if (character.getLevel() > 1) {
                for (int i = 1; i <= hitDie; i++) {
                    sum += i;
                }
                currHP = currHP + ((sum/hitDie)*currLevel);
                character.setCurrentHP(currHP);
                character.setMaxHP(currHP);
                binding.currentHPTextView.setText(String.valueOf(currHP));
                binding.maxHPTextView.setText(String.valueOf(currHP));
            }
        }
    }

    /**
     * getStatModifier calculates stat modifiers for all attributes
     */
    private void getStatModifiers() {
        //get character attributes
        int str = character.getStrength();
        int dex = character.getDexterity();
        int inte = character.getIntelligence();
        int cha = character.getCharisma();
        int con = character.getConstitution();
        int wis = character.getWisdom();

        //calculate statMod for each attribute
        int statStrMod = ((str-10)/2);
        int statDexMod = ((dex-10)/2);
        int statIntMod = ((inte-10)/2);
        int statChaMod = ((cha-10)/2);
        int statConMod = ((con-10)/2);
        int statWisMod = ((wis-10)/2);

        binding.strSMTextView.setText(String.valueOf(statStrMod));
        binding.dexSMTextView.setText(String.valueOf(statDexMod));
        binding.intSMTextView.setText(String.valueOf(statIntMod));
        binding.chaSMTextView.setText(String.valueOf(statChaMod));
        binding.conSMTextView.setText(String.valueOf(statConMod));
        binding.wisSMTextView.setText(String.valueOf(statWisMod));

    }

    public static Intent characterSheetActivityIntentFactory(Context context, CharacterSheet character) {
        Intent intent = new Intent(context, CharacterSheetActivity.class);
        intent.putExtra(CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY, character);
        return intent;
    }
}