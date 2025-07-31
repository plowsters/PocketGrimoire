package com.example.pocketgrimoire;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = AppDatabase.USER_TABLE)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userID;

    private String email;
    private String username;
    private String salt;
    private String hashedPassword;
    private String oauthProvider;
    private Boolean isAdmin;
}