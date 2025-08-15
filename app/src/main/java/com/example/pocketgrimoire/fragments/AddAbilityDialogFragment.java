package com.example.pocketgrimoire.fragments;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Abilities;

public class AddAbilityDialogFragment extends DialogFragment {

    private PocketGrimoireRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repository = new PocketGrimoireRepository((Application) requireContext().getApplicationContext());
        return inflater.inflate(R.layout.fragment_add_ability_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton exitButton = view.findViewById(R.id.exitModalImageButton);
        ImageButton confirmButton = view.findViewById(R.id.confirmImageButton);

        exitButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> addAbility(view));
    }

    private void addAbility(View view) {
        EditText abilityEditText = view.findViewById(R.id.abilityEditText);
        String abilityInput = abilityEditText.getText().toString().trim();

        if (abilityInput.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an ability name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming you have an Ability entity and a corresponding insert method
        Abilities newAbility = new Abilities();
        newAbility.setName(abilityInput);
        repository.insertAbility(newAbility); // You will need to implement this in your repository

        dismiss();
    }
}