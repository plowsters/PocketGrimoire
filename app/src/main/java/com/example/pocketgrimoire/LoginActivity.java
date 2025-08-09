package com.example.pocketgrimoire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pocketgrimoire.databinding.ActivityLoginBinding;
import com.example.pocketgrimoire.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    public static final String TAG = "PocketGrimoireLoginActivity";
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Check if a user is already logged in
        checkUserLoggedIn();

        // Set up the login button click listener
        binding.loginButtonNavigateImageView.setOnClickListener(v -> {
            String username = binding.usernameTextInputEditText.getText().toString().trim();
            String password = binding.passwordTextInputEditText.getText().toString().trim();
            viewModel.login(username, password);
        });

        // Observe the LiveData from the ViewModel
        observeViewModel();
    }

    private void checkUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(getString(R.string.preference_user_id_key), -1);
        if (userId != -1) {
            Intent intent = AdminNavbarActivity.newIntent(LoginActivity.this, userId);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Sets up observers on the LiveData objects from the {@link LoginViewModel}.
     * This method connects the UI of the activity to the state managed by the ViewModel.
     * It listens for two main events:
     * 1.  A successful login event from {@code getLoginSuccess()}, which triggers a success message.
     * 2.  An error event from {@code getErrorMessage()}, which displays the error message to the user.
     * This ensures that the UI automatically reacts to the results of the login process.
     */
    private void observeViewModel() {
        // Observer for login success
        viewModel.getLoginSuccess().observe(this, isSuccess -> {
            if (isSuccess != null) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                // Save the user's ID to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.preference_user_id_key), isSuccess.getUserID());
                editor.apply();
                // Use the Intent Factory to create the intent and start the next activity
                Intent intent = AdminNavbarActivity.newIntent(LoginActivity.this, isSuccess.getUserID());
                startActivity(intent);
                // Finish LoginActivity so the user can't navigate back to it
                finish();
            }
        });

        // Observer for error messages
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}