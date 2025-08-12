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
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.entities.CharacterAbilities;
import com.example.pocketgrimoire.database.entities.CharacterItems;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.CharacterSpells;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.DndApiClient;
import com.example.pocketgrimoire.database.seeding.AbilitiesSeeder;
import com.example.pocketgrimoire.database.seeding.ClassRaceMaps;
import com.example.pocketgrimoire.database.seeding.ItemsSeeder;
import com.example.pocketgrimoire.database.seeding.SpellsSeeder;
import com.example.pocketgrimoire.database.typeConverters.Converters;
import com.example.pocketgrimoire.network.loaders.AbilitiesNetworkLoader;
import com.example.pocketgrimoire.network.loaders.ItemsNetworkLoader;
import com.example.pocketgrimoire.network.loaders.SpellsNetworkLoader;
import com.example.pocketgrimoire.util.PasswordUtils;

import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * The main RoomDB wrapper subclass for the Pocket Grimoire Android App.
 * @Database annotation is so RoomDB can generate concrete methods from this abstract class
 * Uses the singleton pattern to ensure only one instance of the DB is ever created in memory
 */
@TypeConverters({Converters.class})
@Database(entities = {User.class, Items.class, Spells.class, Abilities.class, CharacterSheet.class, CharacterItems.class, CharacterSpells.class, CharacterAbilities.class}, version = 1, exportSchema = false)
public abstract class PocketGrimoireDatabase extends RoomDatabase {
    public static final String DB_NAME = "POCKET_GRIMOIRE_DATABASE";
    public static final String USER_TABLE = "USER_TABLE";
    public static final String CHARACTER_SHEET_TABLE = "CHARACTER_SHEET_TABLE";
    public static final String CHARACTER_ITEMS_TABLE = "CHARACTER_ITEMS_TABLE";
    public static final String CHARACTER_SPELLS_TABLE = "CHARACTER_SPELLS_TABLE";
    public static final String CHARACTER_ABILITIES_TABLE = "CHARACTER_ABILITIES_TABLE";
    public static final String ITEMS_TABLE = "ITEMS_TABLE";
    public static final String SPELLS_TABLE = "SPELLS_TABLE";
    public static final String ABILITIES_TABLE = "ABILITIES_TABLE";

    // volatile = stored in RAM. Necessary to make it visible to all threads
    private static volatile PocketGrimoireDatabase INSTANCE;

    // Singleton function ensuring only one instance of our DB exists in memory
    static PocketGrimoireDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (PocketGrimoireDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
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

            // Build the API service
            DndApiService api = DndApiClient.get();

            // Build network loaders (pure network + mapping, no DB writes)
            ItemsNetworkLoader itemsLoader = new ItemsNetworkLoader(api);
            SpellsNetworkLoader spellsLoader = new SpellsNetworkLoader(api);
            AbilitiesNetworkLoader abilitiesLoader = new AbilitiesNetworkLoader(api);

            // Build seeders they use update and insert methods from the DAOs
            ItemsSeeder itemsSeeder = new ItemsSeeder(itemsLoader, INSTANCE.itemsDAO(), INSTANCE);
            // class/race maps used for availability
            Map<String,String> classes = ClassRaceMaps.defaultClasses();
            Map<String,String> races   = ClassRaceMaps.defaultRaces();
            SpellsSeeder spellsSeeder = new SpellsSeeder(spellsLoader, INSTANCE.spellsDAO(), INSTANCE, classes);
            AbilitiesSeeder abilitiesSeeder = new AbilitiesSeeder(abilitiesLoader, INSTANCE.abilitiesDAO(), INSTANCE, classes, races);

            /* This uses RxJava instead of Executor to make DB queries on a background thread
               Benefits of RxJava over Executor are better lifecycle management, better task scheduling,
               and automatic thread pool allocation.
               A "Completable" is a task (Object) that can be completed but doesn't return anything.
               Great for DB inserts
             */
            Completable seedDefaultUser = Completable.defer(() -> {
                String salt = PasswordUtils.generateSalt();
                String hashedPassword = PasswordUtils.hashPassword("Cleric123!", salt);
                User defaultUser = new User(
                        "dwarfcleric@pocketgrimoire.com",
                        "bobthedwarf",
                        salt,
                        hashedPassword
                );
                return INSTANCE.userDAO().insert(defaultUser);
            });

            // Verify step can query counts and log them at the end
            Completable verifyCounts = Completable.fromAction(() -> {
                Log.i(LoginActivity.TAG, "Seeding complete.");
            });

            // Call the 3 content seeders AFTER the user insert
            // Use andThen to be nice to the API (one seeder completes before the other begins)
            Completable seedAllContent =
                    itemsSeeder.seed()
                            .andThen(spellsSeeder.seed())
                            .andThen(abilitiesSeeder.seed());

            // Run on IO Scheduler. Optionally observeOn main if you want to toast/log on UI
            seedDefaultUser
                    .doOnSubscribe(d -> Log.i(LoginActivity.TAG, "Seeding default user…"))
                    .andThen(itemsSeeder.seed().doOnSubscribe(d -> Log.i(LoginActivity.TAG, "Seeding items…")))
                    .andThen(spellsSeeder.seed().doOnSubscribe(d -> Log.i(LoginActivity.TAG, "Seeding spells…")))
                    .andThen(abilitiesSeeder.seed().doOnSubscribe(d -> Log.i(LoginActivity.TAG, "Seeding abilities…")))
                    .andThen(verifyCounts)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            () -> Log.i(LoginActivity.TAG, "Seeding + verification complete."),
                            t -> Log.e(LoginActivity.TAG, "Seeding failed", t)
                    );
        }
    };

    //RoomDB creates these getter methods for the DAOs for us
    public abstract UserDAO userDAO();
    public abstract CharacterSheetDAO characterSheetDAO();
    public abstract ItemsDAO itemsDAO();
    public abstract SpellsDAO spellsDAO();
    public abstract AbilitiesDAO abilitiesDAO();
    public abstract CharacterItemsDAO characterItemsDAO();
    public abstract CharacterSpellsDAO characterSpellsDAO();
    public abstract CharacterAbilitiesDAO characterAbilitiesDAO();

}
