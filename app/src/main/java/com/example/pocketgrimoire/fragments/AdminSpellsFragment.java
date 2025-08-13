package com.example.pocketgrimoire.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.viewmodel.AdminSpellsViewModel;

public class AdminSpellsFragment extends Fragment {

    private AdminSpellsViewModel mViewModel;

    public static AdminSpellsFragment newInstance() {
        return new AdminSpellsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_spells, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AdminSpellsViewModel.class);
        // TODO: Use the ViewModel
    }

}