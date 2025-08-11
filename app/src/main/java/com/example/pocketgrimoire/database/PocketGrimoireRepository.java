package com.example.pocketgrimoire.database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.pocketgrimoire.LoginActivity;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PocketGrimoireRepository {

    private UserDAO userDAO;
    private CharacterSheetDAO characterSheetDAO;
    private CharacterItemsDAO characterItemsDAO;
    private ItemsDAO itemsDAO;
    private static PocketGrimoireRepository repository;


    /**
     * The constructor for PocketGrimoireRepository
     * Initializes the database and all DAOs
     * @param application - The application context, used to get an instance of the database
     */
    public PocketGrimoireRepository(Application application) {
        PocketGrimoireDatabase db = PocketGrimoireDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.characterSheetDAO = db.characterSheetDAO();
        this.characterItemsDAO = db.characterItemsDAO();
        this.itemsDAO = db.itemsDAO();
    }

    public static Single<PocketGrimoireRepository> getRepository(Application application) {
        return Single.fromCallable(() -> new PocketGrimoireRepository(application))
        .subscribeOn(Schedulers.io());
    }

    /**
     * Retrieves all Users from the DB as a "reactive stream", allowing  the "Subscriber"
     * (module requesting data, typically a LiveData/ViewModel object) to tell the "Publisher"
     * (module providing data, in this case the DAO method) how much data it can handle at once.
     *
     * NOTE: For this to work, your DAO interface method must return a "Flowable", which automatically
     * emits a new list to the Subscriber whenever data in the table changes.
     *
     * @return a Flowable that emits a list of all users
     */
    public Flowable<List<User>> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Retrieves a user by their username from the database.
     * @param username The username of the user to retrieve.
     * @return A Maybe that will emit the User if found, or complete otherwise.
     */
    public Maybe<User> getUserByUsername(String username) {
        return userDAO.getUserByUsername(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Inserts a new user into the DB using RxJava instead of Executor.
     * It defers execution of the userDAO.insert() method to the background threads handled by
     * Schedulers.io(), and the DAO method returns type "Completable" to track a successful or
     * failed completion of the task.
     * @param user
     */
    // Ignore the linter error "Result of .subscribe() never used", not important for DB inserts
    @SuppressLint("CheckResult")
    public void insertUser(User user) {
        // subscribing to this method triggers the Scheduler to execute this operation
        userDAO.insert(user)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Log.i(LoginActivity.TAG, "Insert successful for user: " + user.getUsername()),
                        error -> Log.e(LoginActivity.TAG, "Insert failed for user: " + user.getUsername(), error)
                );
    }

    public Flowable<List<CharacterSheet>> getAllCharacterSheetByUserId(int loggedInUserId) {
        return characterSheetDAO.getAllCharacterSheetByUserID(loggedInUserId);
    }

    public Completable insertCharacterSheet(CharacterSheet character) {
        return characterSheetDAO.insert(character).subscribeOn(Schedulers.io());
    }

    /**
     * deleteCharacter
     */
    public void deleteCharacterSheet(CharacterSheet character) {
        characterSheetDAO.delete(character);
    }
}