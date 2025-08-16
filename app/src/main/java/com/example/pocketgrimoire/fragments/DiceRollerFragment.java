package com.example.pocketgrimoire.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.pocketgrimoire.R;
import com.example.pocketgrimoire.databinding.FragmentDiceRollerBinding;
import com.example.pocketgrimoire.viewmodel.DiceRollerViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;
import java.util.Random;

public class DiceRollerFragment extends BottomSheetDialogFragment {

    private FragmentDiceRollerBinding binding;
    private DiceRollerViewModel vm;

    private int currentSides = 20;
    private final Random rng = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiceRollerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // ViewModel (survives rotation)
        vm = new ViewModelProvider(requireActivity()).get(DiceRollerViewModel.class);
        vm.getResult().observe(getViewLifecycleOwner(),
                text -> binding.textMessage.setText(text));

        // Initial UI state
        if (vm.getResult().getValue() == null) {
            binding.textMessage.setText(getString(R.string.dice_press_hint));
        }
        binding.textDieLabel.setText(getDieLabel(currentSides));
        showSelected(currentSides);

        // Close
        binding.btnClose.setOnClickListener(view -> dismiss());

        // Die selection
        binding.btnD4.setOnClickListener(x -> setDie(4));
        binding.btnD6.setOnClickListener(x -> setDie(6));
        binding.btnD8.setOnClickListener(x -> setDie(8));
        binding.btnD10.setOnClickListener(x -> setDie(10));
        binding.btnD12.setOnClickListener(x -> setDie(12));
        binding.btnD20.setOnClickListener(x -> setDie(20));
        binding.btnD100.setOnClickListener(x -> setDie(100));

        // Roll
        binding.btnRoll.setOnClickListener(x -> roll());
    }

    private void setDie(int sides) {
        currentSides = sides;
        binding.textDieLabel.setText(getDieLabel(sides));
        showSelected(sides);
    }

    private String getDieLabel(int sides) {
        return String.format(Locale.getDefault(), "d%d", sides);
    }

    private void showSelected(int sides) {
        binding.selD4.setVisibility(View.GONE);
        binding.selD6.setVisibility(View.GONE);
        binding.selD8.setVisibility(View.GONE);
        binding.selD10.setVisibility(View.GONE);
        binding.selD12.setVisibility(View.GONE);
        binding.selD20.setVisibility(View.GONE);
        binding.selD100.setVisibility(View.GONE);
        switch (sides) {
            case 4:   binding.selD4.setVisibility(View.VISIBLE); break;
            case 6:   binding.selD6.setVisibility(View.VISIBLE); break;
            case 8:   binding.selD8.setVisibility(View.VISIBLE); break;
            case 10:  binding.selD10.setVisibility(View.VISIBLE); break;
            case 12:  binding.selD12.setVisibility(View.VISIBLE); break;
            case 20:  binding.selD20.setVisibility(View.VISIBLE); break;
            case 100: binding.selD100.setVisibility(View.VISIBLE); break;
        }
    }

    private void roll() {
        int count = parseCount(binding.inputCount.getText() == null
                ? null : binding.inputCount.getText().toString());
        if (count <= 0) count = 1;

        int total = 0, last = 0;
        StringBuilder series = new StringBuilder();

        for (int i = 0; i < count; i++) {
            int val = rng.nextInt(currentSides) + 1;
            last = val; total += val;
            if (i > 0) series.append(" + ");
            series.append(val);
        }

        binding.textBigNumber.setText(String.valueOf(last));

        String msg = getString(R.string.dice_rolled_prefix) + (count == 1 ? last : total);
        // (Optional) show breakdown for multi-rolls:
        // if (count > 1) msg += " (" + series + ")";
        binding.textMessage.setText(msg);
        vm.setResult(msg); // persist via ViewModel
    }

    private int parseCount(@Nullable String s) {
        if (TextUtils.isEmpty(s)) return 1;
        try { return Math.max(1, Integer.parseInt(s.trim())); }
        catch (NumberFormatException e) { return 1; }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid leaks
    }
}