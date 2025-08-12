package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.CharacterItems;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CharacterItemsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(CharacterItems row);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<CharacterItems> rows);

    /** Upsert by composite key. Seeder/repo: if rows==0 then insert(). */
    @Query("UPDATE " + PocketGrimoireDatabase.CHARACTER_ITEMS_TABLE +
            " SET quantity = :quantity, isEquipped = :isEquipped " +
            " WHERE characterID = :characterID AND itemID = :itemID")
    Single<Integer> updateByIds(int characterID, int itemID, int quantity, boolean isEquipped);

    @Query("UPDATE " + PocketGrimoireDatabase.CHARACTER_ITEMS_TABLE +
            " SET quantity = quantity + :delta " +
            " WHERE characterID = :characterID AND itemID = :itemID")
    Single<Integer> incrementQuantityByIds(int characterID, int itemID, int delta);

    @Query("UPDATE " + PocketGrimoireDatabase.CHARACTER_ITEMS_TABLE +
            " SET isEquipped = :isEquipped " +
            " WHERE characterID = :characterID AND itemID = :itemID")
    Single<Integer> setEquippedByIds(int characterID, int itemID, boolean isEquipped);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.CHARACTER_ITEMS_TABLE +
            " WHERE characterID = :characterID ORDER BY itemID")
    Flowable<List<CharacterItems>> getForCharacter(int characterID);

    @Query("DELETE FROM " + PocketGrimoireDatabase.CHARACTER_ITEMS_TABLE +
            " WHERE characterID = :characterID")
    Completable clearForCharacter(int characterID);
}