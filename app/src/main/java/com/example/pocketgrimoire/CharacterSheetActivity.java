package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.databinding.ActivityCharacterSheetBinding;
import com.example.pocketgrimoire.fragments.AccountDialogFragment;
import com.example.pocketgrimoire.fragments.DiceRollerFragment;

import java.io.Serializable;

public class CharacterSheetActivity extends AppCompatActivity {

    private static final String CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY = "CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY";
    private CharacterSheet character;
    private ActivityCharacterSheetBinding binding;
    private PocketGrimoireRepository repository;

    private View utilitiesPopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterSheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        character = (CharacterSheet) getIntent().getSerializableExtra(CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY);

        repository = new PocketGrimoireRepository(getApplication());

        // Find the popup menu and its buttons
        utilitiesPopupMenu = findViewById(R.id.utilities_popup);
        TextView inventoryButton = findViewById(R.id.button_inventory);
        TextView spellbookButton = findViewById(R.id.button_spellbook);
        TextView abilitiesButton = findViewById(R.id.button_abilities);

        // Set onClick listeners for the popup menu items
        inventoryButton.setOnClickListener(v -> {
            Toast.makeText(this, "Inventory Clicked", Toast.LENGTH_SHORT).show(); // Placeholder
            utilitiesPopupMenu.setVisibility(View.GONE); // Hide menu after click
        });

        spellbookButton.setOnClickListener(v -> {
            Toast.makeText(this, "Spellbook Clicked", Toast.LENGTH_SHORT).show(); // Placeholder
            utilitiesPopupMenu.setVisibility(View.GONE); // Hide menu after click
        });

        abilitiesButton.setOnClickListener(v -> {
            Toast.makeText(this, "Abilities Clicked", Toast.LENGTH_SHORT).show(); // Placeholder
            utilitiesPopupMenu.setVisibility(View.GONE); // Hide menu after click
        });

        createBindings();

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_player_menu) {
                // Toggle the visibility of the popup menu
                if (utilitiesPopupMenu.getVisibility() == View.VISIBLE) {
                    utilitiesPopupMenu.setVisibility(View.GONE);
                } else {
                    utilitiesPopupMenu.setVisibility(View.VISIBLE);
                }
                return true;
            }

            // Hide the popup menu if any other nav item is clicked
            if (utilitiesPopupMenu != null) {
                utilitiesPopupMenu.setVisibility(View.GONE);
            }

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
        //display character data top card
        binding.characterNameTextView.setText(character.getCharacterName());
        binding.currentHPTextView.setText(String.valueOf(character.getCurrentHP()));
        binding.raceTextView.setText(character.getRace());
        binding.classTextView.setText(character.getClazz());
        binding.genderTextView.setText(character.getGender());
        calculateArmorClass(); //calculates and display armor class
        showCurrLevel(); //displays current level
        int currLevel = showCurrLevel();
        calculateMaxXP(currLevel); //displays current max xp depending on level
        setLevelListener();
        currentCharXP(); //displays current character xp
        calculateCurrHP(); //displays current character hp
        //attributes
        binding.strValueTextView.setText(String.valueOf(character.getStrength()));
        binding.dexValueTextView.setText(String.valueOf(character.getDexterity()));
        binding.intValueTextView.setText(String.valueOf(character.getIntelligence()));
        binding.chaValueTextView.setText(String.valueOf(character.getCharisma()));
        binding.conValueTextView.setText(String.valueOf(character.getConstitution()));
        binding.wisValueTextView.setText(String.valueOf(character.getWisdom()));
        binding.wisValueTextView.setText(String.valueOf(character.getWisdom()));
        //stat modifiers
        getStatModifiers();
        //characteristics
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
        // AC = 10 + Dexterity Modifier
        int dexMod = (character.getDexterity() - 10) / 2;
        int armorClassValue = 10 + dexMod + currArmor;
        character.setArmorClass(armorClassValue);
        binding.armorClassValueTextView.setText(String.valueOf(character.getArmorClass()));
    }

    private void setLevelListener() {
        binding.currLevelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int currLevel = 1;
                if(charSequence.length() != 0) {
                    currLevel = Integer.parseInt(charSequence.toString());
                }
                calculateMaxXP(currLevel);
                character.setLevel(currLevel);
                repository.insertCharacterSheet(character).blockingGet();
            }
        });
    }

    /**
     * Displays current level
     * Every character will always start with level 1
     * TODO: if there is time, set level to input and change HP and XP accordingly
     * @return
     */
    private int showCurrLevel() {
        int currLevel = 0;
        System.out.println("Current level1: " + character.getLevel());

        if(character.getLevel() == 0) {
            currLevel = 1;
            character.setLevel(currLevel);
            binding.currLevelEditText.setText(String.valueOf(1));
            System.out.println("Current level2: " + character.getLevel());
            return currLevel;
        } else {
            currLevel = character.getLevel();
            binding.currLevelEditText.setText(String.valueOf(currLevel));
            return currLevel;
        }
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
        int conMod = (character.getConstitution() - 10) / 2;

        //level 1
        if (currLevel == 1) {
            currHP = hitDie + conMod;
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