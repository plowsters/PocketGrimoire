package com.example.pocketgrimoire.fragments;

import static androidx.lifecycle.AndroidViewModel_androidKt.getApplication;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.adapter.ItemsListAdapter;
import com.example.pocketgrimoire.database.ItemsDAO;
import com.example.pocketgrimoire.database.PocketGrimoireDatabase;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.databinding.FragmentAddItemDialogBinding;

public class AddItemDialogFragment extends DialogFragment {

    private FragmentAddItemDialogBinding binding;
    private static volatile PocketGrimoireDatabase INSTANCE;
    private PocketGrimoireRepository repository;
    boolean isEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        repository = new PocketGrimoireRepository((Application) getContext().getApplicationContext());
        //Inflate fragment_add_item_dialog layout
        return inflater.inflate(R.layout.fragment_add_item_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ImageButton exitButton = view.findViewById(R.id.exitModalImageButton);
        ImageButton confirmButton = view.findViewById(R.id.confirmImageButton);
        EditText editText = view.findViewById(R.id.itemEditText);
        TextView titleText = view.findViewById(R.id.itemFragmentTextView);

        //get text of edit item
        isEdit =  getArguments() != null;
        if (isEdit) {
            titleText.setText("EDIT ITEM");
            Items editItem = (Items) getArguments().getSerializable("item");
            editText.setText(editItem.getName());
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //for adding item
            public void onClick(View v) {
                if (!isEdit) {
                    addItem(view);
                } else {
                    editItem(view);
                }
            }

        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Avoid memory leaks by making the binding object null
        binding = null;
    }

    private void addItem(View view) {

        EditText itemEditText = view.findViewById(R.id.itemEditText);
        String itemInput = String.valueOf(itemEditText.getText());

        if (itemInput.isEmpty()) {
            Toast.makeText(getContext(), "Please enter item name", Toast.LENGTH_SHORT).show();
        }
        if (!itemInput.isEmpty()) {
            //create a new Items object
            Items newItem = new Items();

            //set new item name to newItems
            newItem.setName(itemInput);

            //add item into object database
            repository.insertItems(newItem).blockingAwait();

            //return to previous activity
            dismiss();
        }
    }

    //For editing
    //Should probably implement isEdit, similar to character creation
    public static AddItemDialogFragment newInstance(Items item) {
        //create an instance of the fragment
        AddItemDialogFragment fragment = new AddItemDialogFragment();
        //create an instance of bundle to pass data between different Android components (hashmap)
        Bundle bundle = new Bundle();

        bundle.putSerializable("item", item); //putSerializable allows us to put objects in a bundle
        //pass bundle we just created above into the fragment
        fragment.setArguments(bundle);
        //save new item name
        return fragment;
    }

    private void editItem(View view) {
        EditText itemEditText = view.findViewById(R.id.itemEditText);
        String itemInput = String.valueOf(itemEditText.getText());

        if (itemInput.isEmpty()) {
            Toast.makeText(getContext(), "Please enter item name", Toast.LENGTH_SHORT).show();
        }
        if (!itemInput.isEmpty()) {
            //get current item
            Items editItem = (Items) getArguments().getSerializable("item");

            //set new item name to newItems
            editItem.setName(itemInput);

            //add item into object database
            repository.updateItems(editItem).blockingAwait();

            //return to previous activity
            dismiss();
        }
    }

    //TODO: exit button

}
