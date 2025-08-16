package com.example.pocketgrimoire;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.database.PocketGrimoireDatabase;
import com.example.pocketgrimoire.database.entities.Abilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AbilityDaoTest {

    private PocketGrimoireDatabase db;
    private AbilitiesDAO dao;

    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, PocketGrimoireDatabase.class)
                .allowMainThreadQueries() // fine for tests
                .build();
        dao = db.abilitiesDAO();

        Abilities a1 = new Abilities();
        a1.setName("Darkvision");
        a1.setTraitOrFeat(true);
        dao.insertSync(a1);

        Abilities a2 = new Abilities();
        a2.setName("Second Wind");
        a2.setTraitOrFeat(false);
        dao.insertSync(a2);

        Abilities a3 = new Abilities();
        a3.setName("Sneak Attack");
        a3.setTraitOrFeat(false);
        dao.insertSync(a3);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void getRandomAbility_returnsSomething() {
        Abilities a = dao.getRandomAbility().blockingGet();
        assertNotNull(a);
        assertNotNull(a.getName());
    }

    @Test
    public void abilitiesCount_matchesInserted() {
        Integer count = dao.abilitiesCount().firstOrError().blockingGet();
        assertEquals(Integer.valueOf(3), count);
    }
}