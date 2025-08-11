package com.example.pocketgrimoire;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.databinding.ActivityCharacterListBinding;
import com.example.pocketgrimoire.adapter.CharacterListAdapter;
import com.example.pocketgrimoire.viewmodel.CharacterListViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CharacterListActivity extends AppCompatActivity {

    private ActivityCharacterListBinding binding;
    private CharacterListViewModel characterListViewModel;
    private int userID;
    public static final int LOGGED_OUT = -1;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get userID
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt(getString(R.string.preference_user_id_key), LOGGED_OUT);

        //display CharacterListActivity and create binding for buttons
        binding = ActivityCharacterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // obtain database
        characterListViewModel = new ViewModelProvider(this).get(com.example.pocketgrimoire.viewmodel.CharacterListViewModel.class);

        //display list of characters based on userID
        RecyclerView recyclerView = binding.characterListDisplayRecyclerView;
        final CharacterListAdapter adapter = new CharacterListAdapter(new CharacterListAdapter.CharacterSheetDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //check for characters under UserID then adds their data
        characterListViewModel.getAllCharacterSheetByUserId(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(characterList -> {
                    adapter.submitList(characterList);
                }, throwable -> {
                    Log.e("RX", "Error loading characters", throwable);
                });
    }
    public static Intent characterListIntentFactory(Context context) {
        Intent intent = new Intent(context, CharacterListActivity.class);
        return intent;
    }

}
