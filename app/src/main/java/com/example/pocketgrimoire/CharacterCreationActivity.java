package com.example.pocketgrimoire;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketgrimoire.database.PocketGrimoireRepository;
import com.example.pocketgrimoire.database.entities.CharacterSheet;
import com.example.pocketgrimoire.databinding.ActivityCharacterCreationBinding;

import java.util.ArrayList;
import java.util.Collections;

public class CharacterCreationActivity extends AppCompatActivity {

    private static final String CHARACTER_EDIT_CHARACTER = "CHARACTER_EDIT_CHARACTER";
    private ActivityCharacterCreationBinding binding;
    private PocketGrimoireRepository repository;
    String mCharacterName = "";
    CharacterSheet character;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterCreationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        character = (CharacterSheet) getIntent().getSerializableExtra(CHARACTER_EDIT_CHARACTER);

        //check if character exists
        assert character != null;
        isEdit();

        repository = PocketGrimoireRepository.getRepository(getApplication()).blockingGet();

        //setCharCreationTitle() to change title for character creation
        createCharCreationTitle();

        //make createCharName() method
        //edit character
        //set character name
        binding.nameEditText.setText(character.getCharacterName());

        createRaceDropdown();
        createClassDropdown();
        createLanguageDropdown();

        //create new object if name is entered (validation) with any chosen parameters
        //if save button pressed
        binding.saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCharacterData();
                //pop current intent
                finish();
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void createRaceDropdown() {
        //populate spinners with database items (dropdown)
        Spinner raceSpinner = (Spinner) binding.raceSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.race_array,
                R.layout.custom_char_create_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raceSpinner.setAdapter(adapter);
        //find position of chosen race tied to character object
        String chosenRace = character.getRace();
        //get the index for this race
        int racePosition = adapter.getPosition(chosenRace);
        //set position in spinner
        raceSpinner.setSelection(racePosition);
    }

    private void createClassDropdown() {
        //populate spinners with database items (dropdown)
        Spinner classSpinner = (Spinner) binding.classSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.class_array,
                R.layout.custom_char_create_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);
        //find position of chosen race tied to character object
        String chosenClass = character.getClazz();
        //get the index for this race
        int classPosition = adapter.getPosition(chosenClass);
        //set position in spinner
        classSpinner.setSelection(classPosition);
    }

    private void createLanguageDropdown(){
        //Languages language_array
//        List<String> chosenLanguages = character.getLanguagesArray();
        binding.languageTextView.setText((CharSequence) character.getLanguages());

        //load language array
        Resources resources = getResources();
        String[] languagesArray = resources.getStringArray(R.array.language_array);
        boolean [] selectedLanguages = new boolean[languagesArray.length];

        ArrayList<Integer> languagesList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(CharacterCreationActivity.this);
        TextView languageChoices = findViewById(R.id.languageTextView);

        // preload chosen languages (need condition for null)
        if (languageChoices != null && character.getLanguages() != null && !character.getLanguages().isEmpty()) {
            for (int i = 0; i < languagesArray.length; i++) {
                if (character.getLanguages().contains(languagesArray[i])) {
                    selectedLanguages[i] = true;
                    languagesList.add(i);
                }
            }
        }

        binding.lanImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        builder.setTitle("Select languages");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(languagesArray, selectedLanguages, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                System.out.println("Length of languageArray on onClick: " + languagesList);
                if (isChecked){
                    languagesList.add(which);
                } else {
                    int posToDel = languagesList.indexOf(which);
                    languagesList.remove(posToDel);
                }
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //create string builder
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 0; i<languagesList.size(); i++) {
                    stringBuilder.append(languagesArray[languagesList.get(i)]);
                    //check condition
                    if (i!=languagesList.size()-1){
                        //when i value not equal to language list size, add a comma
                        stringBuilder.append(", ");
                    }
                    //display chosen languages
                    languageChoices.setText(stringBuilder.toString());
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        }).setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //clear all selected languages on clear click
                for (int i = 0; i < selectedLanguages.length; i++) {
                    selectedLanguages[i] = false;

                    languagesList.clear();
                    languageChoices.setText("");
                }
            }
        });
    }

    /**
     * insertCharacterData() inserts a new character object when a new character is made
     * <br>
     * A character can only be created if the name field is not empty
     */
    private void insertCharacterData() {
        mCharacterName = binding.nameEditText.getText().toString();
        String mRace = binding.raceSpinner.getSelectedItem().toString();
        String mClass = binding.classSpinner.getSelectedItem().toString();
//        int mStr = Integer.parseInt(binding.strAttrEditTextView.getText().toString());
//        int mDex = Integer.parseInt(binding.dexAttrEditTextView.getText().toString());
//        int mInt = Integer.parseInt(binding.intAttrEditTextView.getText().toString());
//        int mCha = Integer.parseInt(binding.chaAttrEditTextView.getText().toString());
//        int mCon = Integer.parseInt(binding.conAttrEditTextView.getText().toString());
//        int mWis = Integer.parseInt(binding.wisAttrEditTextView.getText().toString());
//        String mBg = binding.bgSpinner.getSelectedItem().toString();
        String mLan = binding.languageTextView.getText().toString();
//        String mCharAlign = binding.charAlignSpinner.getSelectedItem().toString();

        String mAge; //input
        String mGender; //spinner
        String mHair; //spinner
        String mEyes; //spinner
        int mHeight; //input
        int mWeight; //input
        String mNotes; //input

        if (mCharacterName.isEmpty()) {
            return;
        }
        character.setCharacterName(mCharacterName);
        character.setRace(mRace);
//        character.setStrength(mStr);
//        character.setDexterity(mDex);
//        character.setIntelligence(mInt);
//        character.setCharisma(mCha);
//        character.setConstitution(mCon);
//        character.setWisdom(mWis);
//        character.setBackground(mBg);
        character.setLanguages(Collections.singletonList(mLan));
        //hashmap
//        character.setAge();
//        hair
//        eye
//        skin
//        height
//        weight
        repository.insertCharacterSheet(character);
    }

    private void isEdit() {
        isEdit = character.getCharacterID() > 0;
    }

    private void createCharCreationTitle() {
        if (isEdit) {
            binding.characterTitleTextView.setText("edit character");
        }
    }

    public static Intent characterAddActivityIntentFactory(Context context, int userID) {
        Intent intent = new Intent(context, CharacterCreationActivity.class);
        CharacterSheet newCharacter = new CharacterSheet();
        newCharacter.setUserID(userID);
        intent.putExtra(CHARACTER_EDIT_CHARACTER, (Parcelable) newCharacter);
        return intent;
    }

    public static Intent characterEditActivityIntentFactory(Context context, CharacterSheet character) {
        Intent intent = new Intent(context, CharacterCreationActivity.class);
        intent.putExtra(CHARACTER_EDIT_CHARACTER, (Parcelable) character);
        return intent;
    }

}
