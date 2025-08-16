package com.example.pocketgrimoire.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Abilities;
import java.util.List;
import io.reactivex.rxjava3.core.Flowable;

public class AdminAbilitiesViewModel extends AndroidViewModel {

    private final PocketGrimoireRepository repository;

    public AdminAbilitiesViewModel(@NonNull Application application) {
        super(application);
        repository = new PocketGrimoireRepository(application);
    }

    /**
     * Gets the list of all abilities from the repository
     * @return A Flowable that emits the full list of Abilities whenever the data changes
     */
    public Flowable<List<Abilities>> getAllAbilitiesList() {
        return repository.getAllAbilitiesList();
    }
}