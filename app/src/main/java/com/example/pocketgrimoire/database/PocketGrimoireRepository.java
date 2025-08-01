package com.example.pocketgrimoire.database;

import android.app.Application;
import android.util.Log;

import com.example.pocketgrimoire.MainActivity;
import com.example.pocketgrimoire.database.entities.User;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PocketGrimoireRepository {

    private UserDAO userDAO;
    private ArrayList<User> allUsers;

    /**
     * The constructor for PocketGrimoireRepository
     * Initializes the database and all DAOs
     * @param application - The application context, used to get an instance of the database
     */
    public PocketGrimoireRepository(Application application) {
        PocketGrimoireDatabase db = PocketGrimoireDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.allUsers = (ArrayList<User>) this.userDAO.getAllUsers();
    }

    /**
     * Retrieves all users from the database
     * Uses Executor to run the DB query on a background thread
     * Uses a Future<> data type to wait for the result of this query
     * @return ArrayList of all User objects, or null if an error occurs
     */
    public ArrayList<User> getAllUsers() {
        // Future stores the result of the asynchronous `Callable` task handled by Executor
        // TODO: Convert to RxJava instead of Executor
        Future<ArrayList<User>> future = PocketGrimoireDatabase.databaseWriteExecutor.submit(
                // Callable task runs on background thread
                new Callable<ArrayList<User>>() {
                    @Override
                    public ArrayList<User> call() throws Exception {
                        return (ArrayList<User>) userDAO.getAllUsers();
                    }
                }
        );
        try {
            return future.get();
        }
        catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
            Log.i(MainActivity.TAG, "Problem when retrieving all users in Repository");
        }
        return null;
    }

    /**
     * Inserts a new user into the database
     * Uses a lambda expression for async tasks, doesn't need a Future since it doesn't return anything
     * @param user
     */
    public void insertUser(User user) {
        PocketGrimoireDatabase.databaseWriteExecutor.execute(()->{
            userDAO.insert(user);
        });
    }

}
