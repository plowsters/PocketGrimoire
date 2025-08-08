package com.example.pocketgrimoire;

import static com.example.pocketgrimoire.CharacterListActivity.LOGGED_OUT;
import static com.example.pocketgrimoire.fragments.UserTypeSelectionFragment.LOGGED_IN_USER_ID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.pocketgrimoire.databinding.ActivityAdminNavbarBinding;

public class AdminNavbarActivity extends AppCompatActivity {

    public static final String USER_ID_KEY = "USER_ID_KEY";

    private ActivityAdminNavbarBinding binding;

    /**
     * An Intent Factory for starting this activity
     * @param context The context of the calling activity
     * @param userId The ID of the user who has logged in
     * @return An Intent to start AdminNavbarActivity
     */
    public static Intent newIntent(Context context, int userId) {
        Intent intent = new Intent(context, AdminNavbarActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminNavbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // You can retrieve the user ID here like this:
        // int userId = getIntent().getIntExtra(USER_ID_KEY, -1);
        // if (userId == -1) { // Handle error, user ID not passed }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.userTypeSelectionFragment) // Add your top-level destinations here
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin_navbar);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Bundle bundle = new Bundle();
        bundle.putInt(LOGGED_IN_USER_ID, getIntent().getIntExtra(USER_ID_KEY, LOGGED_OUT));
        getSupportFragmentManager().setFragmentResult("potato", bundle);
    }
}