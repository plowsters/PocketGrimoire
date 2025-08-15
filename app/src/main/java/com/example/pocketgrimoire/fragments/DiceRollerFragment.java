package com.example.pocketgrimoire.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pocketgrimoire.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;
import java.util.Random;

public class DiceRollerFragment extends BottomSheetDialogFragment {

    private TextView textMessage, textBigNumber, textDieLabel;
    private EditText inputCount;
    private ImageView selD4, selD6, selD8, selD10, selD12, selD20, selD100;

    private int currentSides = 20;
    private final Random rng = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dice_roller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        textMessage   = v.findViewById(R.id.text_message);
        textBigNumber = v.findViewById(R.id.text_big_number);
        textDieLabel  = v.findViewById(R.id.text_die_label);
        inputCount    = v.findViewById(R.id.input_count);

        selD4   = v.findViewById(R.id.sel_d4);
        selD6   = v.findViewById(R.id.sel_d6);
        selD8   = v.findViewById(R.id.sel_d8);
        selD10  = v.findViewById(R.id.sel_d10);
        selD12  = v.findViewById(R.id.sel_d12);
        selD20  = v.findViewById(R.id.sel_d20);
        selD100 = v.findViewById(R.id.sel_d100);

        ImageButton btnClose = v.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(view -> dismiss());

        ImageButton b4=v.findViewById(R.id.btn_d4), b6=v.findViewById(R.id.btn_d6),
                b8=v.findViewById(R.id.btn_d8), b10=v.findViewById(R.id.btn_d10),
                b12=v.findViewById(R.id.btn_d12), b20=v.findViewById(R.id.btn_d20),
                b100=v.findViewById(R.id.btn_d100);
        Button btnRoll = v.findViewById(R.id.btn_roll);

        textMessage.setText(getString(R.string.dice_press_hint));
        textDieLabel.setText(getDieLabel(currentSides));
        showSelected(currentSides);

        b4.setOnClickListener(x -> setDie(4));
        b6.setOnClickListener(x -> setDie(6));
        b8.setOnClickListener(x -> setDie(8));
        b10.setOnClickListener(x -> setDie(10));
        b12.setOnClickListener(x -> setDie(12));
        b20.setOnClickListener(x -> setDie(20));
        b100.setOnClickListener(x -> setDie(100));

        btnRoll.setOnClickListener(x -> roll());
    }

    private void setDie(int sides) {
        currentSides = sides;
        textDieLabel.setText(getDieLabel(sides));
        showSelected(sides);
    }

    private String getDieLabel(int sides) {
        return String.format(Locale.getDefault(), "d%d", sides);
    }

    private void showSelected(int sides) {
        selD4.setVisibility(View.GONE);
        selD6.setVisibility(View.GONE);
        selD8.setVisibility(View.GONE);
        selD10.setVisibility(View.GONE);
        selD12.setVisibility(View.GONE);
        selD20.setVisibility(View.GONE);
        selD100.setVisibility(View.GONE);
        switch (sides) {
            case 4: selD4.setVisibility(View.VISIBLE); break;
            case 6: selD6.setVisibility(View.VISIBLE); break;
            case 8: selD8.setVisibility(View.VISIBLE); break;
            case 10: selD10.setVisibility(View.VISIBLE); break;
            case 12: selD12.setVisibility(View.VISIBLE); break;
            case 20: selD20.setVisibility(View.VISIBLE); break;
            case 100: selD100.setVisibility(View.VISIBLE); break;
        }
    }

    private void roll() {
        int count = parseCount(inputCount.getText()==null ? null : inputCount.getText().toString());
        if (count <= 0) count = 1;

        int total = 0, last = 0;
        StringBuilder series = new StringBuilder();

        for (int i = 0; i < count; i++) {
            int val = rng.nextInt(currentSides) + 1;
            last = val; total += val;
            if (i > 0) series.append(" + ");
            series.append(val);
        }

        textBigNumber.setText(String.valueOf(last));
        textMessage.setText(getString(R.string.dice_rolled_prefix) + (count == 1 ? last : total));
    }

    private int parseCount(@Nullable String s) {
        if (TextUtils.isEmpty(s)) return 1;
        try { return Math.max(1, Integer.parseInt(s.trim())); }
        catch (NumberFormatException e) { return 1; }
    }
}