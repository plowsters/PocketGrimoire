package com.example.pocketgrimoire.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.CharacterSheet;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CharacterSheetDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(CharacterSheet characterSheet);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.CHARACTER_SHEET_TABLE + " WHERE userID = :loggedInUserID")
    Flowable<List<CharacterSheet>> getAllCharacterSheetByUserID(int loggedInUserID);

    @Delete
    Completable delete(CharacterSheet characterSheet);

    @Query(" DELETE FROM " + PocketGrimoireDatabase.CHARACTER_SHEET_TABLE) void deleteAll();
}
