package com.example.pocketgrimoire.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketgrimoire.ItemsActivity;
import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.adapter.CharacterListAdapter;

import io.reactivex.rxjava3.annotations.Nullable;

public class AdminSelectionFragment extends Fragment {

    private int userID;
    public static final int LOGGED_OUT = -1;

    /**
     * constructor
     * @return
     */
    public AdminSelectionFragment(){
        // no argument constructor
    }

    /**
     * Factory method to create new instance of AdminSelectionFragment()
     * @return
     */
    public static AdminSelectionFragment newInstance() {
        return new AdminSelectionFragment();
    }

    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get userID from SharedPreferences using the fragment's context
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt(getString(R.string.preference_user_id_key), LOGGED_OUT);
    }

    /**
     * Creation stage
     * Create and inflate fragments layout for display
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_selection, container, false);
    }


    /**
     * Interaction stage
     * Interactions for fragment UI elements
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find all the buttons in the layout
        ImageButton itemsButton = view.findViewById(R.id.itemsImageButton);
        ImageButton spellsButton = view.findViewById(R.id.spellsImageButton);
        ImageButton abilitiesButton = view.findViewById(R.id.abilitiesImageButton);

        // Set the click listener for the Items button
        itemsButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminPageFragment_to_itemsSelectionFragment)
        );

        // Set the click listener for the Spells button
        spellsButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminPageFragment_to_adminSpellsFragment)
        );

        // Set the click listener for the Abilities button
        abilitiesButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminPageFragment_to_adminAbilitiesFragment)
        );
    }
}
