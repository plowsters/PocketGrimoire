package com.example.pocketgrimoire.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.pocketgrimoire.databinding.FragmentPlayerMenuDialogBinding;

public class UserMenuFragment extends DialogFragment {

    static public String LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID";
    public static final String LOGOUT_EXTRA = "LOGOUT_EXTRA";
    private FragmentPlayerMenuDialogBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @ Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerMenuDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //create bindings for character list, edit character, add new character, signout
    }
}
