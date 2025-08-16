package com.example.pocketgrimoire;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pocketgrimoire.databinding.ActivityRegistrationBinding;
import com.example.pocketgrimoire.viewmodel.RegistrationViewModel;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;
    private RegistrationViewModel viewModel;
    public static final String TAG = "PocketGrimoireRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        // Set OnClickListener for the registration button
        binding.loginButtonNavigateImageView.setOnClickListener(v -> {
            String email = binding.emailTextInputEditText.getText().toString().trim();
            String username = binding.usernameTextInputEditText.getText().toString().trim();
            String password = binding.passwordTextInputEditText.getText().toString().trim();
            String confirmPassword = binding.confirmPasswordTextInputEditText.getText().toString().trim();
            viewModel.registerUser(email, username, password, confirmPassword);
        });

        // Set OnClickListener for the "return to login" text
        binding.loginNavigationTextView.setOnClickListener(v -> {
            // Finish this activity to go back to the LoginActivity on the stack
            finish();
        });

        // Observe ViewModel LiveData for feedback
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getRegistrationSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
                // Finish this activity and return to the login screen
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}