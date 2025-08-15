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
import com.example.pocketgrimoire.database.entities.Spells;

public class AddSpellDialogFragment extends DialogFragment {

    private PocketGrimoireRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repository = new PocketGrimoireRepository((Application) requireContext().getApplicationContext());
        return inflater.inflate(R.layout.fragment_add_spell_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton exitButton = view.findViewById(R.id.exitModalImageButton);
        ImageButton confirmButton = view.findViewById(R.id.confirmImageButton);

        exitButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> addSpell(view));
    }

    private void addSpell(View view) {
        EditText spellEditText = view.findViewById(R.id.spellEditText);
        String spellInput = spellEditText.getText().toString().trim();

        if (spellInput.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a spell name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming you have a Spell entity and a corresponding insert method
        Spells newSpell = new Spells();
        newSpell.setName(spellInput);
        repository.insertSpell(newSpell); // You will need to implement this in your repository

        dismiss();
    }
}