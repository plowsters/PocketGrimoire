package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pocketgrimoire.database.entities.Abilities;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

/**
 * DAO for accessing Abilities table with RxJava support.
 */
@Dao
public interface AbilitiesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAbility(Abilities ability);

    @Update
    Completable updateAbility(Abilities ability);

    @Query("SELECT * FROM abilities")
    Flowable<List<Abilities>> getAllAbilities();

    @Query("SELECT * FROM abilities WHERE enabled = 1")
    Flowable<List<Abilities>> getEnabledAbilities();

    @Query("DELETE FROM abilities")
    Completable clearAbilities();
}