package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.Items;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ItemsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Items item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<Items> items);



    /** Upsert-by-name: update all non-key fields; seeder inserts if this returns 0. */
    @Query("UPDATE " + PocketGrimoireDatabase.ITEMS_TABLE +
            " SET category = :category, isEquippable = :isEquippable WHERE name = :name")
    Single<Integer> updateByName(String name, String category, boolean isEquippable);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.ITEMS_TABLE + " ORDER BY itemID")
    Flowable<List<Items>> getAllItems();

    @Query("SELECT COUNT(*) FROM " + PocketGrimoireDatabase.ITEMS_TABLE)
    Flowable<Integer> itemsCount();

    @Query("DELETE FROM " + PocketGrimoireDatabase.ITEMS_TABLE)
    Completable clearItems();
}
