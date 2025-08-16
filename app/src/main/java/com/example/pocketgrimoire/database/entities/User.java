package com.example.pocketgrimoire.database.entities;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.Objects;

@Entity(tableName = PocketGrimoireDatabase.USER_TABLE)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userID;

    private String email;
    private String username;
    private String salt;
    private String hashedPassword;
    private boolean isAdmin;

    public User() {
        //No argument constructor for Room
    }
    //@Ignore annotation tells RoomDB to use the no parameter constructor, but I can call this
    //constructor manually
    @Ignore
    public User(String email, String username, String salt, String hashedPassword) {
        this.email = email;
        this.username = username;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
        isAdmin = false;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserID() == user.getUserID() && isAdmin() == user.isAdmin() && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getSalt(), user.getSalt()) && Objects.equals(getHashedPassword(), user.getHashedPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserID(), getEmail(), getUsername(), getSalt(), getHashedPassword(), isAdmin());
    }
}