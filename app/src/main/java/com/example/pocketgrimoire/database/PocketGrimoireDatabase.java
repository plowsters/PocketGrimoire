package com.example.pocketgrimoire.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.MainActivity;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class PocketGrimoireDatabase extends RoomDatabase {
    public static final String DB_NAME = "POCKET_GRIMOIRE_DATABASE";
    public static final String USER_TABLE = "USER_TABLE";

    private static volatile PocketGrimoireDatabase INSTANCE;

    // a "getter" for the DAO object returns type UserDAO
    public abstract UserDAO getUserDAO();
}

