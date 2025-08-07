package com.example.pocketgrimoire;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.databinding.ActivityCharacterListBinding;
import com.example.pocketgrimoire.viewmodel.CharacterListAdapter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CharacterListActivity extends AppCompatActivity {

    private ActivityCharacterListBinding binding;
    private com.example.pocketgrimoire.database.viewHolders.CharacterListViewModel characterListViewModel;
    private static final String CHARACTER_LIST_ACTIVITY_USER_ID = "CHARACTER_LIST_ACTIVITY_USER_ID";
    private PocketGrimoireRepository repository;
    private int userID;
    public static final int LOGGED_OUT = -1;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userID = getIntent().getIntExtra(CHARACTER_LIST_ACTIVITY_USER_ID,LOGGED_OUT);


        characterListViewModel = new ViewModelProvider(this).get(com.example.pocketgrimoire.database.viewHolders.CharacterListViewModel.class);

        //display list of characters
        RecyclerView recyclerView = binding.characterListDisplayRecyclerView;
        final CharacterListAdapter adapter = new CharacterListAdapter(new CharacterListAdapter.CharacterSheetDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = PocketGrimoireRepository.getRepository(getApplication()).blockingGet();

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
    public static Intent characterListIntentFactory(Context context, int userID) {
        Intent intent = new Intent(context, CharacterListActivity.class);
        intent.putExtra(CHARACTER_LIST_ACTIVITY_USER_ID, userID);
        return intent;
    }

}
