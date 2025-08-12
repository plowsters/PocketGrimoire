package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.databinding.ActivityCharacterSheetBinding;
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

//        // Only show Dice Roller Fragment for now (bottom half)
//        // Team can add Character Sheet UI later
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container_dice_roller, new DiceRollerFragment())
//                    .commit();
//        }
    }

    private void createBindings() {
        //display character data
        binding.characterNameTextView.setText(character.getCharacterName());
        binding.currentHPTextView.setText(String.valueOf(character.getCurrentHP()));
        //armor class - calculate armor class and display
        //level - everyone starts at level 1
        //lvl 1- 0 exp
        //leveling max is multiples of 3
        binding.raceTextView.setText(character.getRace());
        binding.classTextView.setText(character.getClazz());
        binding.genderTextView.setText(character.getGender());
        binding.strValueTextView.setText(String.valueOf(character.getStrength()));
        binding.dexValueTextView.setText(String.valueOf(character.getDexterity()));
        binding.intValueTextView.setText(String.valueOf(character.getIntelligence()));
        binding.chaValueTextView.setText(String.valueOf(character.getCharisma()));
        binding.conValueTextView.setText(String.valueOf(character.getConstitution()));
        binding.wisValueTextView.setText(String.valueOf(character.getWisdom()));
        binding.wisValueTextView.setText(String.valueOf(character.getWisdom()));
        //stat modifiers
        getStatModifiers();
        binding.backgroundTextView.setText(character.getBackground());
        binding.eyeColorTextView.setText(character.getEyeColor());
        binding.hairColorTextView.setText(character.getHairColor());
        binding.skinColorTextView.setText(character.getSkinColor());
        binding.ageTextView.setText(String.valueOf(character.getAge()));
        binding.heightTextView.setText(String.valueOf(character.getHeight()));
        binding.weightTextView.setText(String.valueOf(character.getWeight()));
        binding.charAlignTextView.setText(character.getCharacterAlignment());
        binding.notesTextView.setText(character.getNotes());
    }
    private void getStatModifiers() {
        int str = character.getStrength();
        int dex = character.getDexterity();
        int inte = character.getIntelligence();
        int cha = character.getCharisma();
        int con = character.getConstitution();
        int wis = character.getWisdom();

        //str stat modifier
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

    private int calculatedHP(){
        return 0;
    }

    public static Intent characterSheetActivityIntentFactory(Context context, CharacterSheet character) {
        Intent intent = new Intent(context, CharacterSheetActivity.class);
        intent.putExtra(CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY, character);
        return intent;
    }
}