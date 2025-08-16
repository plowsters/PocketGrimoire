package com.example.pocketgrimoire.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pocketgrimoire.LoginActivity;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.databinding.FragmentAccountDialogBinding;
import com.example.pocketgrimoire.viewmodel.UserTypeSelectionViewModel;

public class AccountDialogFragment extends DialogFragment {

    public static final String LOGOUT_EXTRA = "LOGOUT_EXTRA";
    private FragmentAccountDialogBinding binding;
    private UserTypeSelectionViewModel viewModel;
    private int userID;
    public static final int LOGGED_OUT = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the logged-in user's ID from SharedPreferences, just like in the other fragment
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt(getString(R.string.preference_user_id_key), LOGGED_OUT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentAccountDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(UserTypeSelectionViewModel.class);

        // Fetch the user's data and update the username TextView
        if (userID != LOGGED_OUT) {
            viewModel.getUser(userID)
                    .subscribe(
                            user -> {
                                // On success, update the TextView with the user's username
                                binding.accountDialogUsername.setText(user.getUsername().toUpperCase());
                            },
                            error -> {
                                // On error, log the issue
                                Log.e("AccountDialog", "Error fetching user", error);
                            }
                    );
        }

        // Set the OnClickListener for the sign out TextView
        binding.signOutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Add a listener to the exit button to close the dialog
        binding.accountDialogExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void logout() {
        // Clear the user ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.preference_user_id_key));
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(LOGOUT_EXTRA, true);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Avoid memory leaks by making the binding object null
        binding = null;
    }
}