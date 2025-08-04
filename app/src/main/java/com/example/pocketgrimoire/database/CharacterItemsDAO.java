package com.example.pocketgrimoire.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.pocketgrimoire.database.entities.CharacterItems;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface CharacterItemsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(CharacterItems characterItems);

    @Delete
    void delete(CharacterItems characterItems);
}
