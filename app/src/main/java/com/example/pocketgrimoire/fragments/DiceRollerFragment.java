package com.example.pocketgrimoire.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.viewmodel.DiceRollerViewModel;

import java.util.Random;

/**
 * Fragment for rolling dice and showing results.
 */
public class DiceRollerFragment extends Fragment {

    private DiceRollerViewModel viewModel;
    private TextView resultView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dice_roller, container, false);

        resultView = view.findViewById(R.id.text_result);
        viewModel = new ViewModelProvider(requireActivity()).get(DiceRollerViewModel.class);

        // Observe the result LiveData
        viewModel.getResult().observe(getViewLifecycleOwner(), result -> resultView.setText(result));

        Button d6Button = view.findViewById(R.id.button_d6);
        d6Button.setOnClickListener(v -> rollDice(6));

        return view;
    }

    private void rollDice(int sides) {
        int result = new Random().nextInt(sides) + 1;
        viewModel.setResult("Rolled D" + sides + ": " + result);
    }
}