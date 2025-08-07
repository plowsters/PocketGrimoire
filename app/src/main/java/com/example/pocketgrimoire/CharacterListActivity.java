package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.databinding.ActivityCharacterListBinding;

public class CharacterListActivity extends AppCompatActivity {

    private ActivityCharacterListBinding binding;
    private com.example.pocketgrimoire.database.viewHolders.CharacterListViewModel characterListViewModel;
    private static final String CHARACTER_LIST_ACTIVITY_USER_ID = "CHARACTER_LIST_ACTIVITY_USER_ID";
    private PocketGrimoireRepository repository;
    private int userID;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityCharacterListBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        userID = getIntent().getIntExtra(CHARACTER_LIST_ACTIVITY_USER_ID,LOGGED_OUT);
//
//
//        characterListViewModel = new ViewModelProvider(this).get(com.example.pocketgrimoire.database.viewHolders.CharacterListViewModel.class);
//
//        //display list of characters
//        RecyclerView recyclerView = binding.characterListDisplayRecyclerView;
//        final CharacterListAdapter adapter = new CharacterListAdapter(new CharacterListAdapter.CharacterSheetDiff());
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        repository = PocketGrimoireRepository.getRepository(getApplication());
//
//        //check for characters under UserID then adds their data
//        characterListViewModel.getAllCharacterSheetByUserId(userID).observe(this, characterList -> {
//            adapter.submitList(characterList); //updates list
//        });
//    }
//    public static Intent characterListIntentFactory(Context context, int userID) {
//        Intent intent = new Intent(context, CharacterListActivity.class);
//        intent.putExtra(CHARACTER_LIST_ACTIVITY_USER_ID, userID);
//        return intent;
//    }

}
