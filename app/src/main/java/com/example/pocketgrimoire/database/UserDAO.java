package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pocketgrimoire.database.entities.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDAO {
    /**
     * Inserts a new user into the database. If a user with the same userID exists, it will
     * be replaced.
     * @param user The User to be inserted.
     * @return A Completable that signals completion or error.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(User user);

    /**
     * Updates an existing user in the database. Uses userID to find and update the current row
     * @param user The updated User.
     * @return A Completable that signals completion or error.
     */
    @Update
    Completable update(User user);

    /**
     * Deletes a user from the database. Uses userID to find and delete a row.
     * @param user The User to be deleted.
     * @return A Completable that signals completion or error.
     */
    @Delete
    Completable delete(User user);

    /**
     * Retrieves all users from the database and stores them in a Flowable (Reactive Stream
     * that supports backpressure on threads for high concurrent reads/writes)..
     * The Flowable will automatically emit a new list whenever the user table changes.
     * @return A Flowable that emits a list of all users.
     */
    @Query("SELECT * FROM " + PocketGrimoireDatabase.USER_TABLE)
    Flowable<List<User>> getAllUsers();

    /**
     * Attempts to find a user by their username.
     * @param username The username to search for.
     * @return A Maybe that will return the User object if found, or complete without an
     * item if no user with that username exists.
     */
    @Query("SELECT * FROM " + PocketGrimoireDatabase.USER_TABLE + " WHERE username = :username")
    Maybe<User> getUserByUsername(String username);

    /**
     * Checks if a specific username already exists in the database. Prevents duplicate usernames.
     * @param username The username to check.
     * @return A Single that emits the number of users with the given username (0 or 1).
     */
    @Query("SELECT COUNT(*) FROM " + PocketGrimoireDatabase.USER_TABLE + " WHERE username = :username")
    Single<Integer> countUsersByUsername(String username);

    /**
     * Checks if a specific email already exists in the database. Prevents duplicate emails.
     * @param email The email to check.
     * @return A Single that emits the number of users with the given email (0 or 1).
     */
    @Query("SELECT COUNT(*) FROM " + PocketGrimoireDatabase.USER_TABLE + " WHERE email = :email")
    Single<Integer> countUsersByEmail(String email);
}
