package com.example.pocketgrimoire.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.database.entities.Items;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public class ItemsListViewModel extends AndroidViewModel {

    private final PocketGrimoireRepository repository;

    /**
     * The CharacterListViewModel class represents a way to query data for the CharacterListActivity
     *
     * @param application
     */
    public ItemsListViewModel(Application application) {
        super(application);
        repository = new PocketGrimoireRepository(application);
    }

    public Flowable<List<Items>> getAllItemsList() {
        return repository.getAllItemsList();
    }

//    public void delete(CharacterSheet character) { repository.deleteCharacterSheet(character); }

}
