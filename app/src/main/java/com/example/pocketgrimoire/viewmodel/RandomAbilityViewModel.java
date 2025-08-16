package com.example.pocketgrimoire.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.Abilities;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RandomAbilityViewModel extends ViewModel {
    private final AbilitiesDAO dao;
    private final CompositeDisposable cd = new CompositeDisposable();

    private final MutableLiveData<Abilities> ability = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public RandomAbilityViewModel(PocketGrimoireRepository repository) { this.dao = repository.getAbilitiesDAO(); }

    public LiveData<Abilities> ability() { return ability; }
    public LiveData<String> error() { return error; }

    /** Fetch one random ability from the database. */
    public void next() {
        cd.add(
                dao.getRandomAbility()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ability::setValue, t -> error.setValue(t.getMessage()))
        );
    }

    @Override protected void onCleared() {
        cd.clear();
    }
}