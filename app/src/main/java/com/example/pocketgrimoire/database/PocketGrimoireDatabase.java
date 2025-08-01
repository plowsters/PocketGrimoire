package com.example.pocketgrimoire.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class PocketGrimoireDatabase extends RoomDatabase {
    public static final String DB_NAME = "POCKET_GRIMOIRE_DATABASE";
    public static final String USER_TABLE = "USER_TABLE";

    private static volatile PocketGrimoireDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static PocketGrimoireDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (PocketGrimoireDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PocketGrimoireDatabase.class,
                            DB_NAME
                    )
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // TODO: add databaseWriteExecutor.execute(() -> {...}
        }
    };

    // a "getter" for the DAO object returns type UserDAO
    public abstract UserDAO getUserDAO();
}

