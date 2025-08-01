package com.example.pocketgrimoire.database;

import android.app.Application;

import com.example.pocketgrimoire.database.entities.User;

import java.util.ArrayList;

public class PocketGrimoireRepository {

    private UserDAO userDAO;
    private ArrayList<User> allUsers;

    public PocketGrimoireRepository(Application application) {
        PocketGrimoireDatabase db = PocketGrimoireDatabase.getDatabase(application);
        this.userDAO = db.UserDAO();
        this.allUsers = (ArrayList<User>) this.userDAO.getAllUsers();
    }
}
