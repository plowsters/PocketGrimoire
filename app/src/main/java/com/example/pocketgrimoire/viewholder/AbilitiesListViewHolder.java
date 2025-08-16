package com.example.pocketgrimoire.viewholder;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.adapter.AbilitiesListAdapter;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Abilities;

public class AbilitiesListViewHolder extends RecyclerView.ViewHolder {

    private final TextView abilityNameTextView;
    private final ImageButton editAbilityButton;
    private final ImageButton deleteAbilityButton;
    private final PocketGrimoireRepository repository;

    public AbilitiesListViewHolder(@NonNull View itemView, Application application) {
        super(itemView);
        abilityNameTextView = itemView.findViewById(R.id.admin_ability_list_text);
        editAbilityButton = itemView.findViewById(R.id.editAbilityImageButton);
        deleteAbilityButton = itemView.findViewById(R.id.deleteAbilityImageButton);
        repository = new PocketGrimoireRepository(application);
    }

    /**
     * Binds an Abilities object to the views in the row
     * @param currentAbility The ability to display
     * @param editListener The listener to handle edit button clicks
     */
    public void bind(Abilities currentAbility, AbilitiesListAdapter.OnItemEditClickListener editListener) {
        abilityNameTextView.setText(currentAbility.getName());

        deleteAbilityButton.setOnClickListener(view -> {
            repository.deleteAbility(currentAbility).subscribe();
        });

        editAbilityButton.setOnClickListener(view -> {
            if (editListener != null) {
                editListener.onEditItem(currentAbility);
            }
        });
    }
}