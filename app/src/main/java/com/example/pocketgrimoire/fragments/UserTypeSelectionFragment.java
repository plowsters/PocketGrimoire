package com.example.pocketgrimoire.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pocketgrimoire.CharacterListActivity;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.viewmodel.UserTypeSelectionViewModel;

public class UserTypeSelectionFragment extends Fragment {


    private UserTypeSelectionViewModel ViewModel;

    static public String LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID";
    private int userID;
    public static final int LOGGED_OUT = -1;

    public static UserTypeSelectionFragment newInstance() {
        return new UserTypeSelectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the logged-in user's ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt(getString(R.string.preference_user_id_key), LOGGED_OUT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_type_selection, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewModel
        ViewModel = new ViewModelProvider(this).get(UserTypeSelectionViewModel.class);

        // Find the TextView for the username
        TextView usernameTextView = view.findViewById(R.id.welcome_username_text_view);

        // Fetch the user data if the user is logged in
        if (userID != LOGGED_OUT) {
            ViewModel.getUser(userID)
                    .subscribe(
                            user -> {
                                // On success, update the TextView with the user's username
                                usernameTextView.setText(user.getUsername().toUpperCase());
                            },
                            error -> {
                                // On error, log the issue
                                Log.e("UserTypeSelection", "Error fetching user", error);
                            }
                    );
        }

        //player button functionality
        ImageView playerButton = view.findViewById(R.id.playerButton);
        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {Intent intent = CharacterListActivity.characterListIntentFactory(requireContext().getApplicationContext());
                startActivity(intent);
            }
        });

        //dungeon master admin button functionality, uses fragment navigation instead of an Intent
        ImageView gameMasterButton = view.findViewById(R.id.gameMasterButton);
        gameMasterButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_userTypeSelectionFragment_to_adminPageFragment)
        );
    }

}