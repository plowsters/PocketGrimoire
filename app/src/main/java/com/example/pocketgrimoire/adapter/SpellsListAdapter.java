package com.example.pocketgrimoire.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.viewholder.SpellsListViewHolder;

public class SpellsListAdapter extends ListAdapter<Spells, SpellsListViewHolder> {

    private final OnItemEditClickListener editClickListener;
    private final Application application;

    public SpellsListAdapter(@NonNull Application application, @NonNull DiffUtil.ItemCallback<Spells> diffCallback, OnItemEditClickListener listener) {
        super(diffCallback);
        this.application = application;
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public SpellsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_spell_list_recycler_item, parent, false);
        return new SpellsListViewHolder(view, application);
    }

    @Override
    public void onBindViewHolder(@NonNull SpellsListViewHolder holder, int position) {
        Spells currentSpell = getItem(position);
        holder.bind(currentSpell, editClickListener);
    }

    // Interface to handle edit clicks in the Fragment
    public interface OnItemEditClickListener {
        void onEditItem(Spells spell);
    }

    // DiffUtil callback to efficiently update the list
    public static class SpellsListDiff extends DiffUtil.ItemCallback<Spells> {
        @Override
        public boolean areItemsTheSame(@NonNull Spells oldItem, @NonNull Spells newItem) {
            // Compare by unique ID
            return oldItem.getSpellID() == newItem.getSpellID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Spells oldItem, @NonNull Spells newItem) {
            // Assumes Spells has a proper .equals() method
            return oldItem.equals(newItem);
        }
    }
}