package com.example.pocketgrimoire;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    public static final String TAG = "PocketGrimoireLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}