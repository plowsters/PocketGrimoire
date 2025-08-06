package com.example.pocketgrimoire.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for Dice Roller.
 * Stores the last rolled result and survives screen rotation.
 */
public class DiceRollerViewModel extends ViewModel {

    private final MutableLiveData<String> result = new MutableLiveData<>("Roll a dice!");

    public LiveData<String> getResult() {
        return result;
    }

    public void setResult(String value) {
        result.setValue(value);
    }
}