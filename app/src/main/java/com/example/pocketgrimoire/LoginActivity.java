package com.example.pocketgrimoire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pocketgrimoire.databinding.ActivityMainBinding;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    public static final String TAG = "PocketGrimoireDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.pocketgrimoire.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
}