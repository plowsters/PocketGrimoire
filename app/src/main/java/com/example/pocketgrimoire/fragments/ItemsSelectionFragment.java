package com.example.pocketgrimoire.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pocketgrimoire.R;

public class ItemsSelectionFragment extends Fragment {

    /**
     * Constructor
     * @return
     */
    public ItemsSelectionFragment(){}

    public static ItemsSelectionFragment newInstance() {
    return new ItemsSelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                      @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_items_fragment, container, false);

    }
}
