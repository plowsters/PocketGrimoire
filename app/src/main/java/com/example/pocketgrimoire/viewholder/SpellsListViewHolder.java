package com.example.pocketgrimoire.viewholder;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.adapter.SpellsListAdapter;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Spells;

public class SpellsListViewHolder extends RecyclerView.ViewHolder {

    private final TextView spellNameTextView;
    private final ImageButton editSpellButton;
    private final ImageButton deleteSpellButton;
    private final PocketGrimoireRepository repository;

    public SpellsListViewHolder(@NonNull View itemView, Application application) {
        super(itemView);
        spellNameTextView = itemView.findViewById(R.id.admin_spell_list_text);
        editSpellButton = itemView.findViewById(R.id.editSpellImageButton);
        deleteSpellButton = itemView.findViewById(R.id.deleteSpellImageButton);
        repository = new PocketGrimoireRepository(application);
    }

    /**
     * Binds a Spells object to the views in the row
     * @param currentSpell The spell to display
     * @param editListener The listener to handle edit button clicks
     */
    public void bind(Spells currentSpell, SpellsListAdapter.OnItemEditClickListener editListener) {
        spellNameTextView.setText(currentSpell.getName());

        // Set the click listener for the delete button
        deleteSpellButton.setOnClickListener(view -> {
            // You will need to add a deleteSpell method to your repository and DAO
            repository.deleteSpell(currentSpell);
        });

        // Set the click listener for the edit button
        editSpellButton.setOnClickListener(view -> {
            if (editListener != null) {
                editListener.onEditItem(currentSpell);
            }
        });
    }
}