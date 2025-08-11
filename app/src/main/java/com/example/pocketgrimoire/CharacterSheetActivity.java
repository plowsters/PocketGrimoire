package com.example.pocketgrimoire;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.databinding.ActivityCharacterSheetBinding;
import com.example.pocketgrimoire.fragments.DiceRollerFragment;

public class CharacterSheetActivity extends AppCompatActivity {

    private ActivityCharacterSheetBinding binding;
    private static final String CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY = "CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY";
    private CharacterSheet character;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterSheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        character = (CharacterSheet) getIntent().getSerializableExtra(CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY);

        //display data into CharacterSheetActivity
        binding.characterNameTextView.setText(character.getCharacterName());
        binding.currentHPTextView.setText(String.valueOf(character.getCurrentHP()));
        binding.raceTextView.setText(character.getRace());
        binding.classTextView.setText(character.getClazz());
        binding.strValueTextView.setText(String.valueOf(character.getStrength()));

//        // Only show Dice Roller Fragment for now (bottom half)
//        // Team can add Character Sheet UI later
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container_dice_roller, new DiceRollerFragment())
//                    .commit();
//        }
    }
}