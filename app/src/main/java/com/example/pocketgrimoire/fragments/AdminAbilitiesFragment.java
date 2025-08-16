package com.example.pocketgrimoire.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.adapter.AbilitiesListAdapter;
import com.example.pocketgrimoire.viewmodel.AdminAbilitiesViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AdminAbilitiesFragment extends Fragment {

    // Member variables for the ViewModel and the Adapter
    private AdminAbilitiesViewModel ViewModel;
    private AbilitiesListAdapter adapter;

    public static AdminAbilitiesFragment newInstance() {
        return new AdminAbilitiesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_abilities, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewModel, which provides the data
        ViewModel = new ViewModelProvider(this).get(AdminAbilitiesViewModel.class);

        // Find the RecyclerView in the layout and set it up to display items in a vertical list
        RecyclerView recyclerView = view.findViewById(R.id.abilityListDisplayRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create an instance of the AbilitiesListAdapter
        adapter = new AbilitiesListAdapter(
                // Pass the application context, required by the ViewHolder to create the repository
                requireActivity().getApplication(),
                // Pass the DiffUtil callback, which helps the adapter efficiently update the list
                new AbilitiesListAdapter.AbilitiesListDiff(),
                // Pass a lambda function to handle edit clicks. This is the implementation of the OnItemEditClickListener interface.
                ability -> {
                    // When an item's edit button is clicked, create a new dialog instance for editing
                    AddAbilityDialogFragment dialog = AddAbilityDialogFragment.newInstance(ability);
                    // Show the dialog
                    dialog.show(getChildFragmentManager(), "EditAbilityDialog");
                }
        );
        // Connect the RecyclerView to the adapter
        recyclerView.setAdapter(adapter);

        // Subscribe to the Flowable stream of abilities from the ViewModel
        ViewModel.getAllAbilitiesList()
                .subscribeOn(Schedulers.io()) // Perform the database query on a background thread
                .observeOn(AndroidSchedulers.mainThread()) // Receive the result on the main UI thread
                .subscribe(
                        // onNext: When a new list is emitted, submit it to the adapter for display
                        abilitiesList -> adapter.submitList(abilitiesList),
                        // onError: If an error occurs, log it
                        throwable -> Log.e("AdminAbilitiesFragment", "Error loading abilities", throwable)
                );

        // Set up the click listener for the "Add New Ability" button
        ImageButton addAbilityButton = view.findViewById(R.id.addAbilityImageButton);
        addAbilityButton.setOnClickListener(v -> {
            // Create a new dialog instance for adding a new ability
            AddAbilityDialogFragment dialogFragment = new AddAbilityDialogFragment();
            // Show the dialog
            dialogFragment.show(getChildFragmentManager(), "AddAbilityDialog");
        });
    }
}