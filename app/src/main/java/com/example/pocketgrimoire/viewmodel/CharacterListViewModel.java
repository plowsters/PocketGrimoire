package com.example.pocketgrimoire.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.CharacterSheet;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public class CharacterListViewModel extends AndroidViewModel{
    private final PocketGrimoireRepository repository;

    /**
     * The CharacterListViewModel class represents a way to query data for the CharacterListActivity
     *
     * @param application
     */
    public CharacterListViewModel(Application application) {
        super(application);
        repository = PocketGrimoireRepository.getRepository(application).blockingGet();
    }

    public Flowable<List<CharacterSheet>> getAllCharacterSheetByUserId(int userID) {
        return repository.getAllCharacterSheetByUserId(userID);
    }

    public void delete(CharacterSheet character) { repository.deleteCharacterSheet(character); }


}

