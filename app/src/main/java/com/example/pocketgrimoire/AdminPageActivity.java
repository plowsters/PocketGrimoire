package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.databinding.ActivityAdminPageBinding;

public class AdminPageActivity extends AppCompatActivity {
    private ActivityAdminPageBinding binding;
    public static final int LOGGED_OUT = -1;
    private int userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get userID
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt(getString(R.string.preference_user_id_key), LOGGED_OUT);

        binding = ActivityAdminPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public static Intent adminPageIntentFactory(Context context) {
        Intent intent = new Intent(context, AdminPageActivity.class);
        return intent;
    }
}
