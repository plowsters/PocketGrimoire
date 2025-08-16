package com.example.pocketgrimoire.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.viewholder.AbilitiesListViewHolder;

public class AbilitiesListAdapter extends ListAdapter<Abilities, AbilitiesListViewHolder> {

    private final OnItemEditClickListener editClickListener;
    private final Application application;

    public AbilitiesListAdapter(@NonNull Application application, @NonNull DiffUtil.ItemCallback<Abilities> diffCallback, OnItemEditClickListener listener) {
        super(diffCallback);
        this.application = application;
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public AbilitiesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_ability_list_recycler_item, parent, false);
        return new AbilitiesListViewHolder(view, application);
    }

    @Override
    public void onBindViewHolder(@NonNull AbilitiesListViewHolder holder, int position) {
        Abilities currentAbility = getItem(position);
        holder.bind(currentAbility, editClickListener);
    }

    public interface OnItemEditClickListener {
        void onEditItem(Abilities ability);
    }

    public static class AbilitiesListDiff extends DiffUtil.ItemCallback<Abilities> {
        @Override
        public boolean areItemsTheSame(@NonNull Abilities oldItem, @NonNull Abilities newItem) {
            return oldItem.getAbilityID() == newItem.getAbilityID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Abilities oldItem, @NonNull Abilities newItem) {
            return oldItem.equals(newItem);
        }
    }
}