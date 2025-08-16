package com.example.pocketgrimoire;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.databinding.ActivityCharacterListBinding;
import com.example.pocketgrimoire.adapter.CharacterListAdapter;
import com.example.pocketgrimoire.fragments.AccountDialogFragment;
import com.example.pocketgrimoire.fragments.DiceRollerFragment;
import com.example.pocketgrimoire.viewmodel.CharacterListViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CharacterListActivity extends AppCompatActivity {

    private ActivityCharacterListBinding binding;
    private CharacterListViewModel characterListViewModel;
    private int userID;
    public static final int LOGGED_OUT = -1;

    private View utilitiesPopupMenu;

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

        // Find the popup menu and its buttons
        utilitiesPopupMenu = findViewById(R.id.utilities_popup);
        TextView inventoryButton = findViewById(R.id.button_inventory);
        TextView spellbookButton = findViewById(R.id.button_spellbook);
        TextView abilitiesButton = findViewById(R.id.button_abilities);

        // Set onClick listeners for the popup menu items
        inventoryButton.setOnClickListener(v -> {
            Toast.makeText(this, "Inventory Clicked", Toast.LENGTH_SHORT).show(); // Placeholder
            utilitiesPopupMenu.setVisibility(View.GONE); // Hide menu after click
        });

        spellbookButton.setOnClickListener(v -> {
            Toast.makeText(this, "Spellbook Clicked", Toast.LENGTH_SHORT).show(); // Placeholder
            utilitiesPopupMenu.setVisibility(View.GONE); // Hide menu after click
        });

        abilitiesButton.setOnClickListener(v -> {
            Toast.makeText(this, "Abilities Clicked", Toast.LENGTH_SHORT).show(); // Placeholder
            utilitiesPopupMenu.setVisibility(View.GONE); // Hide menu after click
        });

        //database
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

        /**
         * Add Character button allows users to add a new character
         */
        binding.addCharacterImageButton.setOnClickListener(view -> {
            Intent intent = CharacterCreationActivity.characterAddActivityIntentFactory(getApplicationContext(), userID);
            startActivity(intent);
        });

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_player_menu) {
                // Toggle the visibility of the popup menu
                if (utilitiesPopupMenu.getVisibility() == View.VISIBLE) {
                    utilitiesPopupMenu.setVisibility(View.GONE);
                } else {
                    utilitiesPopupMenu.setVisibility(View.VISIBLE);
                }
                return true;
            }

            // Hide the popup menu if any other nav item is clicked
            if (utilitiesPopupMenu != null) {
                utilitiesPopupMenu.setVisibility(View.GONE);
            }

            if (itemId == R.id.navigation_account) {
                AccountDialogFragment dialog = new AccountDialogFragment();
                dialog.show(getSupportFragmentManager(), "AccountDialogFragment");
                return true;
            } else if (itemId == R.id.navigation_dice) {
                DiceRollerFragment diceRollerFragment = new DiceRollerFragment();
                diceRollerFragment.show(getSupportFragmentManager(), diceRollerFragment.getTag());
                return true;
            }
            return false;
        });

    }
    public static Intent characterListIntentFactory(Context context) {
        Intent intent = new Intent(context, CharacterListActivity.class);
        return intent;
    }

}
