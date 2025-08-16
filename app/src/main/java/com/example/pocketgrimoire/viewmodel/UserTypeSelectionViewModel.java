package com.example.pocketgrimoire.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.User;
import io.reactivex.rxjava3.core.Maybe;

public class UserTypeSelectionViewModel extends AndroidViewModel {

    private final PocketGrimoireRepository repository;

    public UserTypeSelectionViewModel(@NonNull Application application) {
        super(application);
        repository = new PocketGrimoireRepository(application);
    }

    /**
     * Gets a user by their ID from the repository.
     * @param userId The ID of the user to fetch.
     * @return A Maybe that will emit the User object.
     */
    public Maybe<User> getUser(int userId) {
        return repository.getUserById(userId);
    }
}