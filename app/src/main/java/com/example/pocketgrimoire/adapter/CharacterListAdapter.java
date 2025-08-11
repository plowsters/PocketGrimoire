package com.example.pocketgrimoire.adapter;//package com.example.pocketgrimoire.database.viewHolders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.databinding.CharacterListRecyclerItemBinding;
import com.example.pocketgrimoire.viewholder.CharacterListViewHolder;

/**
 * CharacterListAdapter binds data to views using ViewHolder to handle events
 * Implements all events
 *
 */

public class CharacterListAdapter extends ListAdapter<CharacterSheet, CharacterListViewHolder> {
    private CharacterSheetDiff.OnItemClickListener onItemClickListener;
    CharacterListRecyclerItemBinding binding;
    Context context;

    public CharacterListAdapter (@NonNull DiffUtil.ItemCallback<CharacterSheet> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public CharacterListViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        System.out.println("In adapter oncreateviewholder: " + parent.getContext()); //context is null here

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.character_list_recycler_item, parent, false);
        return new CharacterListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterListViewHolder holder, int position) {
        CharacterSheet current = getItem(position);
        holder.bind(current, context);

    }
    public static class CharacterSheetDiff extends DiffUtil.ItemCallback<CharacterSheet> {
        //are items the same or content the same

        /**
         * are items the same
         * compares memory addresses
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return
         */
        @Override
        public boolean areItemsTheSame(@NonNull CharacterSheet oldItem, @NonNull CharacterSheet newItem) {
            return oldItem == newItem;
        }

        /**
         * Are contents the same
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return
         */
        @Override
        public boolean areContentsTheSame(@NonNull CharacterSheet oldItem, @NonNull CharacterSheet newItem) {
            return oldItem.equals(newItem);
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }
    }
}