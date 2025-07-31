package com.example.pocketgrimoire;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "POCKET_GRIMOIRE_DATABASE";
    public static final String USER_TABLE = "USER_TABLE"

    // a "getter" for the DAO object returns type UserDAO
    public abstract UserDAO getUserDAO();
}

