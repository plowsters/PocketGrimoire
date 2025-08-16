package com.example.pocketgrimoire.viewmodel;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.util.PasswordUtils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegistrationViewModel extends AndroidViewModel {

    private final PocketGrimoireRepository repository;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        repository = new PocketGrimoireRepository(application);
    }

    public LiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void registerUser(String email, String username, String password, String confirmPassword) {
        // Input Validation
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Please fill in all fields.");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Please enter a valid email address.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Passwords do not match.");
            return;
        }

        // Check for existing username and email in the database
        disposable.add(repository.checkUsernameExists(username)
                .flatMap(userCount -> {
                    if (userCount > 0) {
                        throw new Exception("Username is already taken.");
                    }
                    return repository.checkEmailExists(email);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emailCount -> {
                    if (emailCount > 0) {
                        errorMessage.setValue("Email is already registered.");
                        return;
                    }
                    // If validation and checks pass, proceed with registration
                    createNewUser(email, username, password);
                }, error -> {
                    // Handle errors from the chain (e.g., username taken)
                    errorMessage.setValue(error.getMessage());
                })
        );
    }

    private void createNewUser(String email, String username, String password) {
        // Generate salt and hash the password
        String salt = PasswordUtils.generateSalt();
        String hashedPassword = PasswordUtils.hashPassword(password, salt);

        // Create new User object
        User newUser = new User(email, username, salt, hashedPassword);
        // By default, isAdmin is false

        // Insert the user into the database
        repository.insertUser(newUser);
        registrationSuccess.setValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clear disposables to prevent memory leaks
        disposable.clear();
    }
}