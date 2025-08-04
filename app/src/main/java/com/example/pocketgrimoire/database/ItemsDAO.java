package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.Items;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ItemsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Items items);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.ITEMS_TABLE + " ORDER BY itemID")

    Flowable<List<Items>> getAllItems();

    @Query("SELECT COUNT(*) FROM " + PocketGrimoireDatabase.ITEMS_TABLE)
    Flowable<Integer> itemsCount();
}
