package com.example.pocketgrimoire;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.databinding.ActivityRegistrationBinding;


public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;

    public static final String TAG = "PocketGrimoireRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}