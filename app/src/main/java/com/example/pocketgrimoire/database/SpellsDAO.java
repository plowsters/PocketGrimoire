package com.example.pocketgrimoire.database;

import androidx.room.Dao;
<<<<<<< HEAD
import androidx.room.Delete;
=======
>>>>>>> 1bb118f (add Abilities/Spells entities and DAOs from feature/db-tables)
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pocketgrimoire.database.entities.Spells;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

/**
 * DAO for accessing Spells table with RxJava support.
 */
@Dao
public interface SpellsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
<<<<<<< HEAD
    Completable insert(Spells spell);

    @Update
    Completable update(Spells spell);

    @Delete
    Completable delete(Spells spell);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.SPELLS_TABLE)
    Flowable<List<Spells>> getAllSpells();

    @Query("SELECT * FROM " + PocketGrimoireDatabase.SPELLS_TABLE + " WHERE enabled = true")
    Flowable<List<Spells>> getEnabledSpells();

    @Query("DELETE FROM " + PocketGrimoireDatabase.SPELLS_TABLE)
=======
    Completable insertSpell(Spells spell);

    @Update
    Completable updateSpell(Spells spell);

    @Query("SELECT * FROM spells")
    Flowable<List<Spells>> getAllSpells();

    @Query("SELECT * FROM spells WHERE enabled = 1")
    Flowable<List<Spells>> getEnabledSpells();

    @Query("DELETE FROM spells")
>>>>>>> 1bb118f (add Abilities/Spells entities and DAOs from feature/db-tables)
    Completable clearSpells();
}