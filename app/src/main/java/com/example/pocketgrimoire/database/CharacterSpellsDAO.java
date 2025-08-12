package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.CharacterSpells;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CharacterSpellsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(CharacterSpells row);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<CharacterSpells> rows);

    /** Upsert by composite key (used during level gating refresh). */
    @Query("UPDATE " + PocketGrimoireDatabase.CHARACTER_SPELLS_TABLE +
            " SET isPrepared = :isPrepared " +
            " WHERE characterID = :characterID AND spellID = :spellID")
    Single<Integer> updateByIds(int characterID, int spellID, boolean isPrepared);

    @Query("UPDATE " + PocketGrimoireDatabase.CHARACTER_SPELLS_TABLE +
            " SET isPrepared = :isPrepared " +
            " WHERE characterID = :characterID AND spellID = :spellID")
    Single<Integer> setPreparedByIds(int characterID, int spellID, boolean isPrepared);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.CHARACTER_SPELLS_TABLE +
            " WHERE characterID = :characterID ORDER BY spellID")
    Flowable<List<CharacterSpells>> getForCharacter(int characterID);

    @Query("DELETE FROM " + PocketGrimoireDatabase.CHARACTER_SPELLS_TABLE +
            " WHERE characterID = :characterID")
    Completable clearForCharacter(int characterID);
}