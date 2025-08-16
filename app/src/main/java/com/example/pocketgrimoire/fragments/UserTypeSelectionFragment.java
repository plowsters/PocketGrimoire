package com.example.pocketgrimoire.fragments;

import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.pocketgrimoire.AdminPageActivity;
import com.example.pocketgrimoire.CharacterListActivity;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.viewmodel.UserTypeSelectionViewModel;

public class UserTypeSelectionFragment extends Fragment {


    private UserTypeSelectionViewModel mViewModel;

    static public String LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID";

    public static UserTypeSelectionFragment newInstance() {
        return new UserTypeSelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_type_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserTypeSelectionViewModel.class);
        // TODO: Use the ViewModel
    }

}