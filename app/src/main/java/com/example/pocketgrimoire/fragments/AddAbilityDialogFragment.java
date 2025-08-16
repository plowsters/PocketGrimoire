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

    // Keys for passing data (the ability's ID and name) into the fragment's arguments bundle
    private static final String ARG_ABILITY_ID = "ability_id";
    private static final String ARG_ABILITY_NAME = "ability_name";

    private PocketGrimoireRepository repository;
    private EditText abilityEditText;
    private int abilityId = -1; // A default value of -1 indicates we are adding a NEW ability

    /**
     * Factory method to create a new dialog for adding an ability
     */
    public static AddAbilityDialogFragment newInstance() {
        return new AddAbilityDialogFragment();
    }

    /**
     * Factory method to create a new dialog for editing an existing ability
     * @param ability The ability to be edited
     */
    public static AddAbilityDialogFragment newInstance(Abilities ability) {
        AddAbilityDialogFragment fragment = new AddAbilityDialogFragment();
        // Create a bundle to hold the existing ability's data
        Bundle args = new Bundle();
        args.putInt(ARG_ABILITY_ID, ability.getAbilityID());
        args.putString(ARG_ABILITY_NAME, ability.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If arguments were passed (i.e., we are editing), retrieve the ability's ID
        if (getArguments() != null) {
            abilityId = getArguments().getInt(ARG_ABILITY_ID, -1);
        }
        // Initialize the repository
        repository = new PocketGrimoireRepository((Application) requireContext().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_ability_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find all the necessary views in the layout
        abilityEditText = view.findViewById(R.id.abilityEditText);
        ImageButton exitButton = view.findViewById(R.id.exitModalImageButton);
        ImageButton confirmButton = view.findViewById(R.id.confirmImageButton);

        // If we are in edit mode, pre-populate the EditText with the ability's current name
        if (getArguments() != null) {
            String abilityName = getArguments().getString(ARG_ABILITY_NAME);
            abilityEditText.setText(abilityName);
        }

        // Set click listeners for the buttons
        exitButton.setOnClickListener(v -> dismiss()); // Close the dialog
        confirmButton.setOnClickListener(v -> saveAbility()); // Save the changes
    }

    private void saveAbility() {
        String abilityInput = abilityEditText.getText().toString().trim();

        // Validate that the user has entered text
        if (abilityInput.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an ability name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if we are adding a new ability or updating an existing one
        if (abilityId == -1) {
            // ID is -1, so this is a new ability. Create a new object and insert it
            Abilities newAbility = new Abilities();
            newAbility.setName(abilityInput);
            repository.insertAbility(newAbility).subscribe();
        } else {
            // An ID exists, so this is an update. Create an object with the ID and new name
            Abilities existingAbility = new Abilities();
            existingAbility.setAbilityID(abilityId);
            existingAbility.setName(abilityInput);
            repository.updateAbility(existingAbility).subscribe();
        }
        // Close the dialog after saving
        dismiss();
    }
}