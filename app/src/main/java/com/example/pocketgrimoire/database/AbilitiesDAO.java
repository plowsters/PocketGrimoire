package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pocketgrimoire.database.entities.Abilities;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AbilitiesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Abilities ability);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<Abilities> abilities);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertSync(Abilities ability);

    // 1) Update name/flag by name (search by name)
    @Query("UPDATE " + PocketGrimoireDatabase.ABILITIES_TABLE +
            " SET name = :newName, traitOrFeat = :traitOrFeat WHERE name = :searchName")
    Single<Integer> updateNameAndFlagByName(String searchName, String newName, boolean traitOrFeat);

    // 2) Update availability by (name, flag)
    @Query("UPDATE " + PocketGrimoireDatabase.ABILITIES_TABLE +
            " SET availableToClass = :availableToClass WHERE name = :name AND traitOrFeat = 0")
    Single<Integer> updateAvailableToClass(String name, List<String> availableToClass);

    @Query("UPDATE " + PocketGrimoireDatabase.ABILITIES_TABLE +
            " SET availableToRace = :availableToRace WHERE name = :name AND traitOrFeat = 1")
    Single<Integer> updateAvailableToRace(String name, List<String> availableToRace);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.ABILITIES_TABLE + " ORDER BY abilityID")
    Flowable<List<Abilities>> getAllAbilities();

    @Query("SELECT COUNT(*) FROM " + PocketGrimoireDatabase.ABILITIES_TABLE)
    Flowable<Integer> abilitiesCount();

    @Query("DELETE FROM " + PocketGrimoireDatabase.ABILITIES_TABLE)
    Completable clearAbilities();

    @Query("SELECT * FROM " + PocketGrimoireDatabase.ABILITIES_TABLE + " ORDER BY RANDOM() LIMIT 1")
    io.reactivex.rxjava3.core.Single<com.example.pocketgrimoire.database.entities.Abilities> getRandomAbility();
}