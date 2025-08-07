package com.example.pocketgrimoire.database.entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;

import java.util.Objects;

@Entity(tableName = PocketGrimoireDatabase.USER_TABLE)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userID;
    private String oauthSubjectID;


    private String email;
    private String username;
    private String salt;
    private String hashedPassword;
    private String oauthProvider;
    private String authStateJson;
    private boolean isAdmin;

    public User() {
        //No argument constructor for Room
    }
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

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getOauthSubjectID() {
        return oauthSubjectID;
    }

    public void setOauthSubjectID(String oauthSubjectID) {
        this.oauthSubjectID = oauthSubjectID;
    }

    public String getAuthStateJson() {
        return authStateJson;
    }

    public void setAuthStateJson(String authStateJson) {
        this.authStateJson = authStateJson;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserID() == user.getUserID() && isAdmin() == user.isAdmin() && Objects.equals(getOauthSubjectID(), user.getOauthSubjectID()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getSalt(), user.getSalt()) && Objects.equals(getHashedPassword(), user.getHashedPassword()) && Objects.equals(getOauthProvider(), user.getOauthProvider()) && Objects.equals(getAuthStateJson(), user.getAuthStateJson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserID(), getOauthSubjectID(), getEmail(), getUsername(), getSalt(), getHashedPassword(), getOauthProvider(), getAuthStateJson(), isAdmin());
    }
}