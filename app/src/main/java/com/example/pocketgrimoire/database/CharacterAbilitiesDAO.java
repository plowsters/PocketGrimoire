package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pocketgrimoire.database.entities.CharacterAbilities;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface CharacterAbilitiesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(CharacterAbilities ca);

    @Update
    Completable update(CharacterAbilities ca);

    @Query("SELECT * FROM " + PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE + " WHERE characterID = :characterId")
    Flowable<List<CharacterAbilities>> getByCharacterId(int characterId);

    @Query("DELETE FROM " + PocketGrimoireDatabase.CHARACTER_ABILITIES_TABLE + " WHERE characterID = :characterId")
    Completable clearForCharacter(int characterId);
}

