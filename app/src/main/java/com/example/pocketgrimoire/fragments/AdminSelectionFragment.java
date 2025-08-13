package com.example.pocketgrimoire.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pocketgrimoire.R;

import io.reactivex.rxjava3.annotations.Nullable;

public class AdminSelectionFragment extends Fragment {

    public static AdminSelectionFragment newInstance() {
        return new AdminSelectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //items button
        //spells button
        //abilities button
    }
}
