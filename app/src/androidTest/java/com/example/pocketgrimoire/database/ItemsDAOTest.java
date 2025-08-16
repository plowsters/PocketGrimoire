package com.example.pocketgrimoire.database;

import static org.junit.Assert.assertNotEquals;

import android.app.Application;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pocketgrimoire.database.entities.Items;

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
public class ItemsDAOTest extends TestCase {

    ItemsDAO itemsDAO = null;
    private TestScheduler testScheduler;

    @Before
    public void testSetup() {
        //set up phone context for testing
        Application application = ApplicationProvider.getApplicationContext();
        //create
        PocketGrimoireDatabase db = Room.inMemoryDatabaseBuilder(application, PocketGrimoireDatabase.class).build();
        this.itemsDAO = db.itemsDAO();

        //set up rxjava for testing
        testScheduler = new TestScheduler();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testInsert() {
        Items newItem = new Items();
        newItem.setName("new item");
        itemsDAO.insert(newItem).blockingAwait();

        List<Items> result = itemsDAO.getAllItems()
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        assertEquals(1, result.size());
        assertEquals("new item", result.get(0).getName());

        Items newItem2 = new Items();
        newItem2.setName("another item");
        itemsDAO.insert(newItem2).blockingAwait();

        List<Items> result2 = itemsDAO.getAllItems()
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        assertEquals(2, result2.size());
        assertNotEquals("another item", result2.get(0).getName());
        assertEquals("another item", result2.get(1).getName());
    }

    @Test
    public void testGetAllItems() {
        Items newItem = new Items();
        newItem.setName("new item");
        newItem.setItemID(2);
        itemsDAO.insert(newItem).blockingAwait();

        List<Items> result = itemsDAO.getAllItems()
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getItemID());
        assertEquals("new item", result.get(0).getName());
    }

    @Test
    public void testDeleteItem() {
        Items newItem = new Items();
        newItem.setName("new item");
        itemsDAO.insert(newItem).blockingAwait();

        List<Items> result = itemsDAO.getAllItems()
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        newItem = result.get(0);

        itemsDAO.deleteItem(newItem).blockingAwait();

        @NonNull List<Items> deleteResult = itemsDAO.getAllItems()
                .firstOrError()
                .blockingGet(); //waits until Single signals success or exception

        assertEquals(0, deleteResult.size());
    }

    @Test
    public void updateTest() {
        Items newItem = new Items();
        newItem.setName("new item");
        newItem.setCategory("category");
        newItem.setItemID(2);
        itemsDAO.insert(newItem).blockingAwait();

        List<Items> result = itemsDAO.getAllItems()
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        newItem = result.get(0);
        assertEquals("new item", newItem.getName());

        newItem.setCategory("updated category");
        itemsDAO.update(newItem).blockingAwait();

        List<Items> result2 = itemsDAO.getAllItems()
                .subscribeOn(Schedulers.trampoline()) //use trampoline to force synchronous execution
                .observeOn(Schedulers.trampoline())
                .blockingFirst(); //use blockingFirst to force test to wait for result

        assertEquals("updated category", result2.get(0).getCategory());
        assertEquals(false, result2.get(0).isEquippable());
    }
}