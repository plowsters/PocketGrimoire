package com.example.pocketgrimoire.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Spells;
import java.util.List;
import io.reactivex.rxjava3.core.Flowable;

public class AdminSpellsViewModel extends AndroidViewModel {

    private final PocketGrimoireRepository repository;

    public AdminSpellsViewModel(@NonNull Application application) {
        super(application);
        repository = new PocketGrimoireRepository(application);
    }

    /**
     * Gets the list of all spells from the repository
     * @return A Flowable that emits the full list of Spells whenever the data changes
     */
    public Flowable<List<Spells>> getAllSpellsList() {
        return repository.getAllSpellsList();
    }
}