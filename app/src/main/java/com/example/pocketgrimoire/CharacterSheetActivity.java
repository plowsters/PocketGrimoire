package com.example.pocketgrimoire;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.fragments.DiceRollerFragment;

public class CharacterSheetActivity extends AppCompatActivity {

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
}