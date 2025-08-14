package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.databinding.ActivityItemsBinding;

public class ItemsActivity extends AppCompatActivity {

    private ActivityItemsBinding binding;

    //create Items object

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public static Intent itemsIntentFactory(Context context) {
        Intent intent = new Intent(context, ItemsActivity.class);
        return intent;
    }
}

