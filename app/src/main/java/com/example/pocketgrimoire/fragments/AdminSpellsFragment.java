package com.example.pocketgrimoire.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.adapter.SpellsListAdapter;
import com.example.pocketgrimoire.viewmodel.AdminSpellsViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AdminSpellsFragment extends Fragment {

    private AdminSpellsViewModel viewModel;
    private SpellsListAdapter adapter;

    public static AdminSpellsFragment newInstance() {
        return new AdminSpellsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_spells, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(AdminSpellsViewModel.class);

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.spellListDisplayRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create the Adapter, passing a lambda for the edit click listener
        adapter = new SpellsListAdapter(
                requireActivity().getApplication(),
                new SpellsListAdapter.SpellsListDiff(),
                spell -> {
                    AddSpellDialogFragment dialog = AddSpellDialogFragment.newInstance(spell);
                    dialog.show(getChildFragmentManager(), "EditSpellDialog");
                }
        );
        recyclerView.setAdapter(adapter);

        // Observe the data from the ViewModel and submit it to the adapter
        viewModel.getAllSpellsList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        spellsList -> adapter.submitList(spellsList),
                        throwable -> Log.e("AdminSpellsFragment", "Error loading spells", throwable)
                );

        // Set up the "Add New Spell" button
        ImageButton addSpellButton = view.findViewById(R.id.addSpellImageButton);
        addSpellButton.setOnClickListener(v -> {
            AddSpellDialogFragment dialogFragment = new AddSpellDialogFragment();
            dialogFragment.show(getChildFragmentManager(), "AddSpellDialog");
        });
    }
}