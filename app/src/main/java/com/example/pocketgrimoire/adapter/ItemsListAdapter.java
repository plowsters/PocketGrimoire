package com.example.pocketgrimoire.adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.databinding.AdminItemsRecyclerItemBinding;
import com.example.pocketgrimoire.viewholder.ItemsListViewHolder;

public class ItemsListAdapter extends ListAdapter<Items, ItemsListViewHolder> {

    private final OnItemEditClickListener editClickListener;
    private ItemsListDiff.OnItemClickListener onItemClickListner;
    AdminItemsRecyclerItemBinding binding;
    Context context;
    Application application;

    public ItemsListAdapter(@NonNull DiffUtil.ItemCallback<Items> diffCallback, OnItemEditClickListener listener) {
        super(diffCallback);
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public ItemsListViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.admin_items_recycler_item, parent, false);
        return new ItemsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull ItemsListViewHolder holder, int position) {
        Items currentItem = getItem(position);
        holder.bind(currentItem, context, application, editClickListener);
    }

    public interface OnItemEditClickListener {
        void onEditItem(Items item);
    }

    public static class ItemsListDiff extends DiffUtil.ItemCallback<Items> {

        /**
         * are items the same
         * compares memory addresses
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return boolean
         */
        @Override
        public boolean areItemsTheSame(@NonNull Items oldItem, @NonNull Items newItem) {
            return oldItem == newItem;
        }

        /**
         * Are contents the same
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return boolean
         */
        @Override
        public boolean areContentsTheSame(@NonNull Items oldItem, @NonNull Items newItem) {
            return oldItem.equals(newItem);
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }
    }
}
