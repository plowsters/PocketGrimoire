package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pocketgrimoire.database.entities.CharacterSpells;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface CharacterSpellsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(CharacterSpells cs);

    @Update
    Completable update(CharacterSpells cs);
}