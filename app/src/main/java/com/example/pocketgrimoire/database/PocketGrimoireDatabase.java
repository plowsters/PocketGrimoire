package com.example.pocketgrimoire.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.pocketgrimoire.LoginActivity;
import com.example.pocketgrimoire.database.typeConverters.Converters;
import com.example.pocketgrimoire.util.PasswordUtils;
import com.example.pocketgrimoire.database.entities.CharacterItems;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.entities.CharacterSpells;
import com.example.pocketgrimoire.database.entities.CharacterAbilities;

import com.example.pocketgrimoire.database.SpellsDAO;
import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.database.CharacterSpellsDAO;
import com.example.pocketgrimoire.database.CharacterAbilitiesDAO;
import com.example.pocketgrimoire.database.CharacterSheetDAO;
import com.example.pocketgrimoire.database.CharacterItemsDAO;
import com.example.pocketgrimoire.database.ItemsDAO;

import com.example.pocketgrimoire.database.typeConverters.Converters;
import com.example.pocketgrimoire.util.PasswordUtils;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * The main RoomDB wrapper subclass for the Pocket Grimoire Android App.
 * @Database annotation is so RoomDB can generate concrete methods from this abstract class
 * Uses the singleton pattern to ensure only one instance of the DB is ever created in memory
 */
@TypeConverters({Converters.class})
@Database(
        entities = {User.class, CharacterSheet.class, CharacterItems.class, Items.class, Spells.class, Abilities.class, CharacterSpells.class, CharacterAbilities.class},
        version = 6,
        exportSchema = false
)
public abstract class PocketGrimoireDatabase extends RoomDatabase {
    public static final String DB_NAME = "POCKET_GRIMOIRE_DATABASE";
    public static final String USER_TABLE = "USER_TABLE";
    public static final String CHARACTER_SHEET_TABLE = "CHARACTER_SHEET_TABLE";
    public static final String CHARACTER_ITEMS_TABLE = "CHARACTER_ITEMS_TABLE";
    public static final String ITEMS_TABLE = "ITEMS_TABLE";
    public static final String CHARACTER_SPELLS_TABLE = "CHARACTER_SPELLS_TABLE";
    public static final String CHARACTER_ABILITIES_TABLE = "CHARACTER_ABILITIES_TABLE";

    // volatile = stored in RAM. Necessary to make it visible to all threads
    private static volatile PocketGrimoireDatabase INSTANCE;

    // Singleton function ensuring only one instance of our DB exists in memory
    static PocketGrimoireDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PocketGrimoireDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    PocketGrimoireDatabase.class,
                                    DB_NAME
                            )
                            // .fallbackToDestructiveMigration() is deprecated, using it anyways
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * A Callback that is executed upon creation of the database
     * Here is where you want to pre-populate your tables with default entries
     * (i.e. default admin user, default items, default spells, default abilities)
     */
    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
        // Ignore the linter error "Result of .subscribe() never used", not important for DB inserts
        @SuppressLint("CheckResult")
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.i(LoginActivity.TAG, "DATABASE CREATED");

            /* This uses RxJava instead of Executor to make DB queries on a background thread
               Benefits of RxJava over Executor are better lifecycle management, better task scheduling,
               and automatic thread pool allocation.
               A "Completable" is a task (Object) that can be completed but doesn't return anything.
               Great for DB inserts
             */
            Completable.fromAction(() -> {
                        UserDAO userDao = INSTANCE.userDAO();

                        // Seed a default user (from main)
                        String salt = PasswordUtils.generateSalt();
                        String hashedPassword = PasswordUtils.hashPassword("Cleric123!", salt);
                        User defaultUser = new User("dwarfcleric@pocketgrimoire.com", "bobthedwarf", salt, hashedPassword);
                        defaultUser.setUserID(1);
                        // blockingAwait() ensures this is added before any other I/O on the DB
                        userDao.insert(defaultUser).blockingAwait();

                        // Insert a sample character for that user (from main)
                        CharacterSheetDAO characterDAO = INSTANCE.characterSheetDAO();
                        CharacterSheet character1 = new CharacterSheet();
                        character1.setCharacterID(1);
                        //add userid for user who owns character1
                        character1.setUserID(defaultUser.getUserID());
                        //hard code a character for testing
                        //display character1 on character list
                        character1.setCharacterName("character1");
                        character1.setRace("dragonborn");
                        character1.setClazz("bard");
                        System.out.println("Creating new character: " + character1.toString());
                        characterDAO.insert(character1).blockingAwait();

                        // (Optional) You could also seed spells/abilities here later if needed.
                    })
                    // Schedulers.io provides a thread pool for I/O operations, in this case DB access
                    .subscribeOn(Schedulers.io())
                    // Subscribing triggers the scheduler to execute I/O operations
                    .subscribe(
                            () -> Log.i(LoginActivity.TAG, "Default users inserted successfully."),
                            error -> Log.e(LoginActivity.TAG, "Error inserting default users", error)
                    );
        }
    };

    // RoomDB creates these getter methods for the DAOs for us
    public abstract UserDAO userDAO();
    public abstract CharacterSheetDAO characterSheetDAO();
    public abstract ItemsDAO itemsDAO();
    public abstract CharacterItemsDAO characterItemsDAO();
    public abstract SpellsDAO spellsDAO();
    public abstract AbilitiesDAO abilitiesDAO();
    public abstract CharacterSpellsDAO characterSpellsDAO();
    public abstract CharacterAbilitiesDAO characterAbilitiesDAO();

}