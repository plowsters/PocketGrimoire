package com.example.pocketgrimoire.database;

import static org.junit.Assert.assertNotEquals;

import android.app.Application;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.User;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.schedulers.TestScheduler;

@RunWith(AndroidJUnit4.class)
public class CharacterSheetDAOTest extends TestCase {

    private CharacterSheetDAO characterSheetDAO = null;

    private UserDAO userDAO;
    private TestScheduler testScheduler;

    @Before
    public void testSetup() {
        //set up phone context for testing
        Application application = ApplicationProvider.getApplicationContext();
        //create
        PocketGrimoireDatabase db = Room.inMemoryDatabaseBuilder(application, PocketGrimoireDatabase.class)
            .allowMainThreadQueries()
            .addCallback(new RoomDatabase.Callback() {
                //foreign keys
                public void onConfigure(@NonNull SupportSQLiteDatabase db) {
                    db.setForeignKeyConstraintsEnabled(true);
                }
            })
            .build();

        this.characterSheetDAO = db.characterSheetDAO();
        this.userDAO = db.userDAO();

        //set up rxjava for testing
        testScheduler = new TestScheduler();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testInsert() {
        User newUser = new User(); //make new user
        newUser.setUsername("newusername");
        newUser.setEmail("newEmail@test.com");
        newUser.setHashedPassword("password");
        userDAO.insert(newUser).blockingAwait();

        User insertedUser = userDAO.getUserByUsername("newusername").blockingGet();
        int userID = insertedUser.getUserID();

        CharacterSheet newCharacter = new CharacterSheet(); //make new character
        newCharacter.setUserID(userID);
        newCharacter.setCharacterName("new character name");
        characterSheetDAO.insert(newCharacter).blockingGet();

        List<CharacterSheet> result = characterSheetDAO.getAllCharacterSheetByUserID(userID)
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        assertEquals(1, result.size());
        assertEquals("new character name", result.get(0).getCharacterName());

        CharacterSheet newCharacter2 = new CharacterSheet();
        newCharacter2.setUserID(userID);
        newCharacter2.setCharacterName("another character name");
        characterSheetDAO.insert(newCharacter2).blockingGet();

        List<CharacterSheet> result2 = characterSheetDAO.getAllCharacterSheetByUserID(userID)
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        assertEquals(2, result2.size());
        assertNotEquals("another character name", result2.get(0).getCharacterName());
        assertEquals("another character name", result2.get(1).getCharacterName());
    }

    @Test
    public void testGetAllCharacterSheetByUserID() {
    }

    @Test
    public void testDelete() {
        User newUser = new User(); //make new user
        newUser.setUsername("newusername");
        newUser.setEmail("newEmail@test.com");
        newUser.setHashedPassword("password");
        userDAO.insert(newUser).blockingAwait();

        User insertedUser = userDAO.getUserByUsername("newusername").blockingGet();
        int userID = insertedUser.getUserID();

        CharacterSheet newCharacter = new CharacterSheet(); //make new character
        newCharacter.setUserID(userID);
        newCharacter.setCharacterName("new character name");
        characterSheetDAO.insert(newCharacter).blockingGet();

        List<CharacterSheet> result = characterSheetDAO.getAllCharacterSheetByUserID(userID)
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        newCharacter = result.get(0);
        characterSheetDAO.delete(newCharacter).blockingAwait();

        @NonNull List<CharacterSheet> deleteResult = characterSheetDAO.getAllCharacterSheetByUserID(userID)
                .firstOrError()
                .blockingGet(); //waits until Single signals success or exception

        assertEquals(0, deleteResult.size());
    }
}