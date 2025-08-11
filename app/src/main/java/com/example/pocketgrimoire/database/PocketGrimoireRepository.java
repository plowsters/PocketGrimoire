package com.example.pocketgrimoire.database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.example.pocketgrimoire.LoginActivity;
import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.entities.Abilities;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * The central repository for the Pocket Grimoire app.
 * Uses the Room-generated DAOs to read/write data, and exposes RxJava types
 * so ViewModels can subscribe safely on background threads.
 *
 * NOTE: This class intentionally keeps "fire-and-forget" inserts for some ops
 * (subscribe() without observing result) since the UI doesn't need to block.
 */
public class PocketGrimoireRepository {

    // --- DAOs provided by the Room database ---
    private UserDAO userDAO;
    private CharacterSheetDAO characterSheetDAO;
    private CharacterItemsDAO characterItemsDAO;
    private ItemsDAO itemsDAO;
    private SpellsDAO spellsDAO;
    private AbilitiesDAO abilitiesDAO;

    // Optional singleton access if needed by callers that prefer Single<> factory
    private static PocketGrimoireRepository repository;

    /**
     * The constructor for PocketGrimoireRepository
     * Initializes the database and all DAOs
     * @param application - The application context, used to get an instance of the database
     */
    public PocketGrimoireRepository(Application application) {
        PocketGrimoireDatabase db = PocketGrimoireDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.characterSheetDAO = db.characterSheetDAO();
        this.characterItemsDAO = db.characterItemsDAO();
        this.itemsDAO = db.itemsDAO();
        this.spellsDAO = db.spellsDAO();
        this.abilitiesDAO = db.abilitiesDAO();
    }

    /**
     * Optional helper that returns a Repository on a background thread.
     * Useful if a caller wants to lazily obtain the repo with Rx semantics.
     */
    public static Single<PocketGrimoireRepository> getRepository(Application application) {
        return Single.fromCallable(() -> new PocketGrimoireRepository(application))
                .subscribeOn(Schedulers.io());
    }

    // =========================================================================
    // Users
    // =========================================================================

    /**
     * Retrieves all Users from the DB as a "reactive stream", allowing the "Subscriber"
     * (typically a ViewModel) to receive updates whenever the table changes.
     */
    public Flowable<List<User>> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Retrieves a user by username. Returns Maybe<User> (0 or 1 result).
     */
    public Maybe<User> getUserByUsername(String username) {
        return userDAO.getUserByUsername(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Inserts a new user into the DB using RxJava instead of Executor.
     * It defers execution of the userDAO.insert() method to Schedulers.io().
     */
    @SuppressLint("CheckResult")
    public void insertUser(User user) {
        userDAO.insert(user)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Log.i(LoginActivity.TAG, "Insert successful for user: " + user.getUsername()),
                        error -> Log.e(LoginActivity.TAG, "Insert failed for user: " + user.getUsername(), error)
                );
    }

    // =========================================================================
    // Character Sheets
    // =========================================================================

    /**
     * Returns all CharacterSheets for a specific user as a Flowable.
     * ViewModels can observe this to refresh the UI automatically.
     */
    public Flowable<List<CharacterSheet>> getAllCharacterSheetByUserId(int loggedInUserId) {
        return characterSheetDAO.getAllCharacterSheetByUserID(loggedInUserId);
    }

    /**
     * Inserts (or replaces) a CharacterSheet. Wrapped in Completable for consistency
     * with other insert methods that already return a Completable.
     */
    public Completable insertCharacterSheet(CharacterSheet characterSheet) {
        return Completable.fromAction(() -> characterSheetDAO.insert(characterSheet))
                .subscribeOn(Schedulers.io());
    }

    // =========================================================================
    // Items (Master List)
    // =========================================================================

    /**
     * Get all items from the master list. Emits again when DB changes.
     */
    public Flowable<List<Items>> getAllItems() {
        return itemsDAO.getAllItems().subscribeOn(Schedulers.io());
    }

    /**
     * Insert/replace a single item in the master list.
     */
    public Completable insertItem(Items item) {
        // ItemsDAO defines insert(Items)
        return itemsDAO.insert(item).subscribeOn(Schedulers.io());
    }

    /**
     * Utility to check how many items are currently in the master list table.
     */
    public Flowable<Integer> getItemsCount() {
        return itemsDAO.itemsCount().subscribeOn(Schedulers.io());
    }

    // =========================================================================
    // Spells (Master List)
    // =========================================================================

    public Flowable<List<Spells>> getAllSpells() {
        return spellsDAO.getAllSpells().subscribeOn(Schedulers.io());
    }

    public Flowable<List<Spells>> getEnabledSpells() {
        return spellsDAO.getEnabledSpells().subscribeOn(Schedulers.io());
    }

    public Completable insertSpell(Spells spell) {
        // SpellsDAO defines insertSpell(Spells)
        return spellsDAO.insertSpell(spell).subscribeOn(Schedulers.io());
    }

    public Completable updateSpell(Spells spell) {
        return spellsDAO.updateSpell(spell).subscribeOn(Schedulers.io());
    }

    public Completable clearSpells() {
        return spellsDAO.clearSpells().subscribeOn(Schedulers.io());
    }

    // =========================================================================
    // Abilities (Master List)
    // =========================================================================

    public Flowable<List<Abilities>> getAllAbilities() {
        return abilitiesDAO.getAllAbilities().subscribeOn(Schedulers.io());
    }

    public Flowable<List<Abilities>> getEnabledAbilities() {
        return abilitiesDAO.getEnabledAbilities().subscribeOn(Schedulers.io());
    }

    public Completable insertAbility(Abilities ability) {
        // AbilitiesDAO defines insertAbility(Abilities)
        return abilitiesDAO.insertAbility(ability).subscribeOn(Schedulers.io());
    }

    public Completable updateAbility(Abilities ability) {
        return abilitiesDAO.updateAbility(ability).subscribeOn(Schedulers.io());
    }

    public Completable clearAbilities() {
        return abilitiesDAO.clearAbilities().subscribeOn(Schedulers.io());
    }
}