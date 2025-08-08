package com.example.pocketgrimoire.fragments;

import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.pocketgrimoire.CharacterListActivity;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.viewmodel.UserTypeSelectionViewModel;

public class UserTypeSelectionFragment extends Fragment {


    private UserTypeSelectionViewModel mViewModel;

    static public String LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID";
    private int loggedID;

    public static UserTypeSelectionFragment newInstance() {
        return new UserTypeSelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_type_selection, container, false);
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up FragmentResultListener
        getParentFragmentManager().setFragmentResultListener("potato", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                //Retrieve data from bundle (userID)
                loggedID = bundle.getInt(LOGGED_IN_USER_ID);
                System.out.println("This is the loggedID: " + loggedID);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //find the button
        ImageView playerButton = view.findViewById(R.id.playerButton);
        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                System.out.println("This is the loggedID onClick: " + loggedID);
                Intent intent = CharacterListActivity.characterListIntentFactory(requireContext().getApplicationContext(), loggedID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserTypeSelectionViewModel.class);
        // TODO: Use the ViewModel
    }

}