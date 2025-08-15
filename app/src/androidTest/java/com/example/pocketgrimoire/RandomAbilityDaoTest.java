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
        ability.setName("Darkvision");
        ability.setTraitOrFeat(true);
        ability.setAvailableToClass(Collections.singletonList("Rogue"));
        ability.setAvailableToRace(Collections.singletonList("Elf"));

        abilitiesDAO.insertSync(ability);

        List<Abilities> all = abilitiesDAO.getAllAbilities().blockingFirst();
        assertNotNull(all);
        assertEquals(1, all.size());
        assertEquals("Darkvision", all.get(0).getName());
    }
}