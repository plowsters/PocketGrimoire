package com.example.pocketgrimoire.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.adapter.CharacterListAdapter;
import com.example.pocketgrimoire.adapter.ItemsListAdapter;
import com.example.pocketgrimoire.viewholder.ItemsListViewHolder;
import com.example.pocketgrimoire.viewmodel.ItemsListViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ItemsSelectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemsListAdapter adapter; //make adapter
    private ItemsListViewModel itemsListViewModel;
    /**
     * Constructor
     * @return
     */
    public ItemsSelectionFragment(){}

    public static ItemsSelectionFragment newInstance() {
    return new ItemsSelectionFragment();
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                      @Nullable Bundle savedInstanceState) {
        //Inflate layout for items fragment - initiates binding
        View view = inflater.inflate(R.layout.activity_items_fragment, container, false);
        recyclerView = view.findViewById(R.id.itemListDisplayRecycleView);

        //display list of characters based on userID
        adapter = new ItemsListAdapter(new ItemsListAdapter.ItemsListDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //database
        itemsListViewModel = new ViewModelProvider(this).get(com.example.pocketgrimoire.viewmodel.ItemsListViewModel.class);

        //get items and save them under UserID
        //from there the UserID can delete or add new items
        //check for characters under UserID then adds their data
        itemsListViewModel.getAllItemsList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(itemsList -> {
                adapter.submitList(itemsList);
            }, throwable -> {
                Log.e("RX", "Error loading items", throwable);
            });

        //Add Item Dialog Fragment
        ImageButton showAddItemDialogButton = view.findViewById(R.id.addItemImageButton);

        showAddItemDialogButton.setOnClickListener( v -> {
            AddItemDialogFragment addItemDialogFragment = new AddItemDialogFragment();
            addItemDialogFragment.show(getChildFragmentManager(), "AddItemDialogFragment");
        });

        return view;

    }
}
