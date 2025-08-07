package com.example.pocketgrimoire.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.util.PasswordUtils;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class LoginViewModel extends AndroidViewModel {

    private final PocketGrimoireRepository repository;

    // This object lets us free the RxJava subscription from memory when we are done observing an
    // Observable or other subclass of an Observable
    private final CompositeDisposable disposables = new CompositeDisposable();

    // LiveData for communicating with the UI
    private final MutableLiveData<User> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /**
     * Constructor for the LoginViewModel
     * @param application The application context, used to instantiate the RoomDB repository
     */
    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new PocketGrimoireRepository(application);
    }

    /**
     * The observer within the activity uses this to decide what to do based on successful
     * or unsuccessful logins
     * @return A LiveData object that emits the User object upon successful login
     */
    public LiveData<User> getLoginSuccess() {
        return loginSuccess;
    }

    /**
     * The observer within the activity uses this to display an error message to the user upon
     * an unsuccessful login
     * @return A LiveData object that emits error message strings
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * User login method
     * @param username The username entered by the user
     * @param password The password entered by the user
     */
    public void login(String username, String password) {
        // Basic input validation
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            errorMessage.setValue("Username and password cannot be empty.");
            return;
        }

        // Add the subscription to the CompositeDisposable to be managed by the ViewModel's lifecycle
        disposables.add(
                // Fetch the user from the repository. The repository handles background threading
                repository.getUserByUsername(username)
                .subscribe(
                        user -> {
                            // If the salted password matches the hash
                            if (PasswordUtils.verifyPassword(password, user.getHashedPassword(), user.getSalt())) {
                                // notify the activity of a successful login
                                loginSuccess.setValue(user);
                            } else {
                                // otherwise, invalid login credentials
                                errorMessage.setValue("Invalid username or password.");
                                loginSuccess.setValue(null);
                            }
                        },
                        // if an exception occurs during the verification process
                        error -> {
                            errorMessage.setValue("An error occurred during login.");
                            loginSuccess.setValue(null);
                        },
                        // if no user was found with that username, throw error
                        () -> {
                            errorMessage.setValue("Invalid username or password.");
                            loginSuccess.setValue(null);
                        }
                ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Dispose of all subscriptions to prevent memory leaks
        disposables.clear();
    }
}