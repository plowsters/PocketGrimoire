package com.example.pocketgrimoire.viewholder;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.CharacterCreationActivity;
import com.example.pocketgrimoire.CharacterSheetActivity;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.CharacterSheet;

/**
 * CharacterListViewHolder handles implementation of each item in the RecycleView
 *
 * The bind function displays the character name and button image that the character resides on
 * The edit and delete buttons are also displayed alongside the character
 * The user can either view the Character sheet of chosen character, edit the character, or delete the character
 */

public class CharacterListViewHolder extends RecyclerView.ViewHolder {

    //references to xml assets
    private final TextView characterListItemTextview;
    private final ImageButton characterListImageButton;
    private final ImageButton editCharacterImageButton;
    private final ImageButton deleteCharacterImageButton;

    public CharacterListViewHolder(@NonNull View characterListView) {
        super(characterListView);
        characterListItemTextview = characterListView.findViewById(R.id.characterListItemTextview);
        characterListImageButton = characterListView.findViewById(R.id.characterListImageButton);
        editCharacterImageButton = characterListView.findViewById(R.id.editCharacterImageButton);
        deleteCharacterImageButton = characterListView.findViewById(R.id.deleteCharacterImageButton);
    }

    public void bind(CharacterSheet currentCharacter, Context context, Application application) {
        System.out.println("currentCharacter " + currentCharacter);
        characterListItemTextview.setText(currentCharacter.getCharacterName());
        characterListImageButton.setOnClickListener(view -> {
            Intent intent = CharacterSheetActivity.characterSheetActivityIntentFactory(context.getApplicationContext(), currentCharacter);
            context.startActivity(intent);
        });

        /**
         * Edit button allows users to edit their chosen character
         */
        editCharacterImageButton.setOnClickListener(view -> {
            Intent intent = CharacterCreationActivity.characterEditActivityIntentFactory(context.getApplicationContext(), currentCharacter);
            context.startActivity(intent);
        });

        /**
         * Delete button allows users to delete a chosen character
         */
        deleteCharacterImageButton.setOnClickListener(view -> {
            System.out.println("This is the delete button");
            PocketGrimoireRepository repository = new PocketGrimoireRepository(application);
            repository.deleteCharacterSheet(currentCharacter);
        });
    }
}
