package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.CharacterAbilities;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CharacterAbilitiesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(CharacterAbilities row);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<CharacterAbilities> rows);

    /** Idempotent delete for corrections (e.g., level rollback or resync). */
    @Query("DELETE FROM " + PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE +
            " WHERE characterID = :characterID AND abilityID = :abilityID")
    Completable deleteByIds(int characterID, int abilityID);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE +
            " WHERE characterID = :characterID ORDER BY abilityID")
    Flowable<List<CharacterAbilities>> getForCharacter(int characterID);

    @Query("DELETE FROM " + PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE +
            " WHERE characterID = :characterID")
    Completable clearForCharacter(int characterID);
}