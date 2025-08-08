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

    /**
     * Base upsert pass: search by name and set name/flag.
     * Using WHERE name=:name allows correcting the flag if it changed.
     */
    @Query("UPDATE " + PocketGrimoireDatabase.ABILITIES_TABLE +
            " SET name = :name, traitOrFeat = :traitOrFeat WHERE name = :name")
    Single<Integer> updateByNameAndFlag(String name, boolean traitOrFeat);

    /**
     * Availability pass: use (name, flag) as the identity to set the list fields.
     * Traits (flag=true): availableToClass should typically be NULL (or empty list),
     * Features (flag=false): availableToRace should typically be NULL (or empty list).
     */
    @Query("UPDATE " + PocketGrimoireDatabase.ABILITIES_TABLE +
            " SET availableToClass = :availableToClass, availableToRace = :availableToRace " +
            "WHERE name = :name AND traitOrFeat = :traitOrFeat")
    Single<Integer> updateAvailabilityByNameAndFlag(String name,
                                                    boolean traitOrFeat,
                                                    List<String> availableToClass,
                                                    List<String> availableToRace);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.ABILITIES_TABLE + " ORDER BY abilityID")
    Flowable<List<Abilities>> getAllAbilities();

    @Query("SELECT COUNT(*) FROM " + PocketGrimoireDatabase.ABILITIES_TABLE)
    Flowable<Integer> abilitiesCount();

    @Query("DELETE FROM " + PocketGrimoireDatabase.ABILITIES_TABLE)
    Completable clearAbilities();
}