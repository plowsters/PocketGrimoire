package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.Spells;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SpellsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Spells spell);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<Spells> spells);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertSync(Spells spell);

    /** Update-by-name: update the entire row's non-key fields; seeder inserts if this returns 0. */
    @Query("UPDATE " + PocketGrimoireDatabase.SPELLS_TABLE +
            " SET level = :level, school = :school, availableToClass = :availableToClass WHERE name = :name")
    Single<Integer> updateByName(String name, int level, String school, List<String> availableToClass);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.SPELLS_TABLE + " ORDER BY spellID")
    Flowable<List<Spells>> getAllSpells();

    @Query("SELECT COUNT(*) FROM " + PocketGrimoireDatabase.SPELLS_TABLE)
    Flowable<Integer> spellsCount();

    @Query("DELETE FROM " + PocketGrimoireDatabase.SPELLS_TABLE)
    Completable clearSpells();
}