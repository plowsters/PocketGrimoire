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
import com.example.pocketgrimoire.viewmodel.AdminAbilitiesViewModel;

public class AdminAbilitiesFragment extends Fragment {

    private AdminAbilitiesViewModel mViewModel;

    public static AdminAbilitiesFragment newInstance() {
        return new AdminAbilitiesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_abilities, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AdminAbilitiesViewModel.class);
        // TODO: Use the ViewModel
    }

}