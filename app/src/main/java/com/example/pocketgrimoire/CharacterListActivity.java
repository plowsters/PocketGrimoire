package com.example.pocketgrimoire;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.databinding.ActivityCharacterListBinding;

public class CharacterListActivity extends AppCompatActivity {

    private ActivityCharacterListBinding binding;
    private com.example.pocketgrimoire.database.viewHolders.CharacterListViewModel characterListViewModel;
    private static final String CHARACTER_LIST_ACTIVITY_USER_ID = "CHARACTER_LIST_ACTIVITY_USER_ID";
    private PocketGrimoireRepository repository;
    private int userID;
}
