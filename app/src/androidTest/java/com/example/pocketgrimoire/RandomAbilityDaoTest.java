package com.example.pocketgrimoire;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RandomAbilityDaoTest {

    private AbilitiesDAO abilitiesDAO;
    private PocketGrimoireDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, PocketGrimoireDatabase.class)
                .allowMainThreadQueries()
                .build();
        abilitiesDAO = db.abilitiesDAO();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testInsertAndRetrieveRandomAbility() {
        Abilities ability = new Abilities();
        ability.setName("TestAbilityOne");
        ability.setTraitOrFeat(true);
        ability.setAvailableToClass(Collections.singletonList("TestClass"));
        ability.setAvailableToRace(Collections.singletonList("TestRace"));
        abilitiesDAO.insertSync(ability);

        List<Abilities> all = abilitiesDAO.getAllAbilities().blockingFirst();
        assertNotNull(all);
        assertEquals(1, all.size());
        assertEquals("TestAbilityOne", all.get(0).getName());
    }

    @Test
    public void testGetRandomAbilityReturnsOneOfInserted() {
        Abilities a1 = new Abilities();
        a1.setName("TestAbilityOne");
        a1.setTraitOrFeat(true);

        Abilities a2 = new Abilities();
        a2.setName("TestAbilityTwo");
        a2.setTraitOrFeat(false);

        abilitiesDAO.insertSync(a1);
        abilitiesDAO.insertSync(a2);

        Abilities random = abilitiesDAO.getRandomAbility().blockingGet();

        assertNotNull(random);
        String n = random.getName();
        assertTrue(
                "Random ability should be one of the inserted names",
                "TestAbilityOne".equals(n) || "TestAbilityTwo".equals(n)
        );
    }
}