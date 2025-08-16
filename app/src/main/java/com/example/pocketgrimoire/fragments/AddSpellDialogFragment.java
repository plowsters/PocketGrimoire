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

    private static final String ARG_SPELL_ID = "spell_id";
    private static final String ARG_SPELL_NAME = "spell_name";

    private PocketGrimoireRepository repository;
    private EditText spellEditText;
    private int spellId = -1; // Default to -1 to indicate a new spell

    /**
     * Factory method to create a new dialog for adding a spell
     */
    public static AddSpellDialogFragment newInstance() {
        return new AddSpellDialogFragment();
    }

    /**
     * Factory method to create a new dialog for editing an existing spell
     * @param spell The spell to be edited
     */
    public static AddSpellDialogFragment newInstance(Spells spell) {
        AddSpellDialogFragment fragment = new AddSpellDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SPELL_ID, spell.getSpellID());
        args.putString(ARG_SPELL_NAME, spell.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spellId = getArguments().getInt(ARG_SPELL_ID, -1);
        }
    }

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

        spellEditText = view.findViewById(R.id.spellEditText);
        ImageButton exitButton = view.findViewById(R.id.exitModalImageButton);
        ImageButton confirmButton = view.findViewById(R.id.confirmImageButton);

        // If we are editing, pre-populate the EditText
        if (getArguments() != null) {
            String spellName = getArguments().getString(ARG_SPELL_NAME);
            spellEditText.setText(spellName);
        }

        exitButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> saveSpell());
    }

    private void saveSpell() {
        String spellInput = spellEditText.getText().toString().trim();

        if (spellInput.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a spell name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spellId == -1) {
            // This is a new spell, so we insert it
            Spells newSpell = new Spells();
            newSpell.setName(spellInput);
            repository.insertSpell(newSpell).subscribe();
        } else {
            // This is an existing spell, so we update it.
            Spells existingSpell = new Spells();
            existingSpell.setSpellID(spellId);
            existingSpell.setName(spellInput);
            repository.updateSpell(existingSpell).subscribe();
        }
        dismiss();
    }
}