package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.fragments.DiceRollerFragment;

import java.io.Serializable;

public class CharacterSheetActivity extends AppCompatActivity {

    private static final String CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY = "CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY";
    private CharacterSheet character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);

        // Only show Dice Roller Fragment for now (bottom half)
        // Team can add Character Sheet UI later
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_dice_roller, new DiceRollerFragment())
                    .commit();
        }
    }

    public static Intent characterSheetActivityIntentFactory(Context context, CharacterSheet character) {
        Intent intent = new Intent(context, CharacterSheetActivity.class);
        intent.putExtra(CHARACTER_SHEET_ACTIVITY_CHARACTER_KEY, (Parcelable) character);
        return intent;
    }
}