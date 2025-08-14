package com.example.pocketgrimoire.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pocketgrimoire.ItemsActivity;
import com.example.pocketgrimoire.R;

import io.reactivex.rxjava3.annotations.Nullable;

public class AdminSelectionFragment extends Fragment {

    /**
     * constructor
     * @return
     */
    public AdminSelectionFragment(){}

    /**
     * Factory method to create new instance of AdminSelectionFragment()
     * @return
     */
    public static AdminSelectionFragment newInstance() {
        return new AdminSelectionFragment();
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

        //items button
        ImageView itemButton = view.findViewById(R.id.itemsImageButton);
        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ItemsActivity.itemsIntentFactory(requireContext().getApplicationContext());
                startActivity(intent);
            }
        });
        //spells button
        //abilities button
    }
}
