package com.example.pocketgrimoire.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.viewmodel.AdminAbilitiesViewModel;

public class AdminAbilitiesFragment extends Fragment {

    private AdminAbilitiesViewModel ViewModel;

    public static AdminAbilitiesFragment newInstance() {
        return new AdminAbilitiesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_abilities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModel = new ViewModelProvider(this).get(AdminAbilitiesViewModel.class);
        // TODO: Use the ViewModel to display the list of abilities in your RecyclerView

        // Find the "add ability" button and set its click listener
        ImageButton addAbilityButton = view.findViewById(R.id.addAbilityImageButton);
        addAbilityButton.setOnClickListener(v -> {
            AddAbilityDialogFragment dialogFragment = new AddAbilityDialogFragment();
            dialogFragment.show(getChildFragmentManager(), "AddAbilityDialog");
        });
    }
}