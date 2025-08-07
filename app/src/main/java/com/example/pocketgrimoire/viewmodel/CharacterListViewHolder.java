package com.example.pocketgrimoire.viewmodel;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.entities.CharacterSheet;

/**
 * CharacterListViewHolder handles implementation of each item in the RecycleView
 *
 * The bind function displays the character name and button image that the character resides on
 * The edit and delete buttons are also displayed alongside the character
 * The user can either view the Character sheet of chosen character, edit the character, or delete the character
 */

public class CharacterListViewHolder extends RecyclerView.ViewHolder {

    private final TextView characterListItemTextview; //reference to xml

    public CharacterListViewHolder(@NonNull View characterListView) {
        super(characterListView);
        characterListItemTextview = characterListView.findViewById(R.id.characterListItemTextview); //find characterListItemTextview in recycleritem
    }

    /**
     * set text to be displayed on the item and add clicklistener on each item
     * to change to CharacterSheetActivity
     * @param
     */
    public void bind(CharacterSheet currentCharacter, Context context) {
        System.out.println("currentCharacter " + currentCharacter);
        characterListItemTextview.setText(currentCharacter.getCharacterName());

    }
}
