package com.example.pocketgrimoire.viewholder;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Items;

public class ItemsListViewHolder extends RecyclerView.ViewHolder {

    private final TextView itemsListItemTextview;
//    private final ImageButton itemsListImageButton; ?
    private final ImageButton editItemImageButton;
    private final ImageButton deleteItemImageButton;

    public ItemsListViewHolder(@NonNull View itemsListView) {
        super(itemsListView);
        itemsListItemTextview = itemsListView.findViewById(R.id.itemsListItemTextview);
        editItemImageButton = itemsListView.findViewById(R.id.editItemImageButton);
        deleteItemImageButton = itemsListView.findViewById(R.id.deleteItemImageButton);

    }

    public void bind(Items currentItem, Context context, Application application) {
        itemsListItemTextview.setText(currentItem.getName());

        /**
         * Delete button allows users to delete a chosen character
         * Is this deleting from the database?
         */
        deleteItemImageButton.setOnClickListener(view -> {
            PocketGrimoireRepository repository = new PocketGrimoireRepository(application);
            repository.deleteItem(currentItem);
        });
    }

}
