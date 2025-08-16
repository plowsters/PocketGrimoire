package com.example.pocketgrimoire.database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.example.pocketgrimoire.LoginActivity;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.entities.CharacterItems;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.database.remote.CharacterDataService;
import com.example.pocketgrimoire.database.remote.DndApiClient;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PocketGrimoireRepository {

    private UserDAO userDAO;
    private CharacterSheetDAO characterSheetDAO;
    private CharacterItemsDAO characterItemsDAO;
    private CharacterSpellsDAO characterSpellsDAO;
    private CharacterAbilitiesDAO characterAbilitiesDAO;
    private ItemsDAO itemsDAO;
    private SpellsDAO spellsDAO;
    private AbilitiesDAO abilitiesDAO;

    private final CharacterDataService characterDataService;


    /**
     * The constructor for PocketGrimoireRepository
     * Initializes the database and all DAOs
     * @param application - The application context, used to get an instance of the database
     */
    public PocketGrimoireRepository(Application application) {
        PocketGrimoireDatabase db = PocketGrimoireDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.itemsDAO = db.itemsDAO();
        this.spellsDAO = db.spellsDAO();
        this.abilitiesDAO = db.abilitiesDAO();
        this.characterSheetDAO = db.characterSheetDAO();
        this.characterItemsDAO = db.characterItemsDAO();
        this.characterSpellsDAO = db.characterSpellsDAO();
        this.characterAbilitiesDAO = db.characterAbilitiesDAO();

        //Initialize the CharacterDataService, giving it the DAOs it needs for seeding starting equipment
        this.characterDataService = new CharacterDataService(
                DndApiClient.get(),
                db.itemsDAO(),
                db.spellsDAO(),
                db.abilitiesDAO()
        );
    }

    public AbilitiesDAO getAbilitiesDAO() {
        return abilitiesDAO;
    }

    /**
     * Retrieves all Users from the DB as a "reactive stream", allowing  the "Subscriber"
     * (module requesting data, typically a LiveData/ViewModel object) to tell the "Publisher"
     * (module providing data, in this case the DAO method) how much data it can handle at once
     *
     * NOTE: For this to work, your DAO interface method must return a "Flowable", which automatically
     * emits a new list to the Subscriber whenever data in the table changes
     *
     * @return a Flowable that emits a list of all users
     */
    public Flowable<List<User>> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Retrieves a user by their username from the database
     * @param username The username of the user to retrieve
     * @return A Maybe that will emit the User if found, or complete otherwise
     */
    public Maybe<User> getUserByUsername(String username) {
        return userDAO.getUserByUsername(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Inserts a new user into the DB using RxJava instead of Executor
     * It defers execution of the userDAO.insert() method to the background threads handled by
     * Schedulers.io(), and the DAO method returns type "Completable" to track a successful or
     * failed completion of the task
     * @param user
     */
    // Ignore the linter error "Result of .subscribe() never used", not important for DB inserts
    @SuppressLint("CheckResult")
    public void insertUser(User user) {
        // subscribing to this method triggers the Scheduler to execute this operation
        userDAO.insert(user)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Log.i(LoginActivity.TAG, "Insert successful for user: " + user.getUsername()),
                        error -> Log.e(LoginActivity.TAG, "Insert failed for user: " + user.getUsername(), error)
                );
    }

    public Flowable<List<CharacterSheet>> getAllCharacterSheetByUserId(int loggedInUserId) {
        return characterSheetDAO.getAllCharacterSheetByUserID(loggedInUserId);
    }

    public Single<Long> insertCharacterSheet(CharacterSheet character) {
        return characterSheetDAO.insert(character).subscribeOn(Schedulers.io());
    }

    /**
     * deleteCharacter
     */
    public void deleteCharacterSheet(CharacterSheet character) {
        characterSheetDAO.delete(character).subscribeOn(Schedulers.io()).blockingAwait();
    }

    /**
     * Gets starting equipment for a class
     */
    public Single<List<CharacterItems>> fetchStartingEquipmentForClass(String classIndex, int characterId) {
        return characterDataService.fetchStartingEquipmentForClass(classIndex, characterId)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Inserts items into CharacterItems table
     */
    public Completable insertCharacterItems(List<CharacterItems> items) {
        return characterItemsDAO.insertAll(items).subscribeOn(Schedulers.io());
    }

    /**
     * Gets all items from Items table
     * Flowable returns data
     * @return
     */
    public Flowable<List<Items>> getAllItemsList() {
        return itemsDAO.getAllItems().subscribeOn(Schedulers.io());
    }

    /**
     * deleteItem
     */
    public void deleteItem(Items itemName) {
        itemsDAO.deleteItem(itemName).subscribeOn(Schedulers.io()).blockingAwait();
    }

    /**
     * Inserts Items
     * Completable checks if for completion or error
     * @param items
     */
    public Completable insertItems(Items items) {
       return itemsDAO.insert(items).subscribeOn(Schedulers.io());
    }

    public Flowable<List<Spells>> getAllSpellsList() {
        return spellsDAO.getAllSpells().subscribeOn(Schedulers.io());
    }

    /**
     * Inserts a new Spell into the database
     * @param spell The Spell object to insert
     * @return A Completable that signals completion or error
     */
    public Completable insertSpell(Spells spell) {
        return spellsDAO.insert(spell).subscribeOn(Schedulers.io());
    }

    public Completable updateSpell(Spells spell) {
        return spellsDAO.update(spell).subscribeOn(Schedulers.io());
    }

    public Completable deleteSpell(Spells spell) {
        return spellsDAO.delete(spell).subscribeOn(Schedulers.io());
    }

    public Flowable<List<Abilities>> getAllAbilitiesList() {
        return abilitiesDAO.getAllAbilities().subscribeOn(Schedulers.io());
    }

    /**
     * Inserts a new Ability into the database
     * @param ability The Ability object to insert
     * @return A Completable that signals completion or error
     */
    public Completable insertAbility(Abilities ability) {
        return abilitiesDAO.insert(ability).subscribeOn(Schedulers.io());
    }

    public Completable updateAbility(Abilities ability) {
        return abilitiesDAO.update(ability).subscribeOn(Schedulers.io());
    }

    public Completable deleteAbility(Abilities ability) {
        return abilitiesDAO.delete(ability).subscribeOn(Schedulers.io());
    }
}