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
import android.widget.Toast;

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

        repository = new PocketGrimoireRepository(getApplication());

        //setCharCreationTitle() to change title for character creation
        createCharCreationTitle();

        //edit character
        //set character name
        binding.nameEditText.setText(character.getCharacterName());
        binding.strAttrEditTextView.setText(String.valueOf(character.getStrength()));
        binding.dexAttrEditTextView.setText(String.valueOf(character.getDexterity()));
        binding.intAttrEditTextView.setText(String.valueOf(character.getIntelligence()));
        binding.chaAttrEditTextView.setText(String.valueOf(character.getCharisma()));
        binding.conAttrEditTextView.setText(String.valueOf(character.getConstitution()));
        binding.wisAttrEditTextView.setText(String.valueOf(character.getWisdom()));
        binding.ageEditText.setText(String.valueOf(character.getAge()));
        binding.heightEditText.setText(String.valueOf(character.getHeight()));
        binding.weightEditText.setText(String.valueOf(character.getWeight()));
        binding.notesEditText.setText(character.getNotes());
        createRaceDropdown();
        createClassDropdown();
        createBackgroundDropdown();
        createLanguageDropdown();
        createCharAlignDropDown();
        createGenderDropDown();
        createEyeColorDropDown();
        createHairColorDropDown();
        createSkinColorDropDown();

        /**
         * A new character object will be saved when the Save button is pressed
         * Save will only occur if a character name has been answered
         */
        //TODO: Create a toast that will trigger if a character name has not been entered
        binding.saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCharacterData();
                finish(); //pop current intent
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * classes to create dropdown items for all dropdown components
     */
    private void createRaceDropdown() {
        //populate spinners with database items (dropdown)
        Spinner raceSpinner = (Spinner) binding.raceSpinner;
        ArrayAdapter<CharSequence> raceAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.race_array,
                R.layout.custom_char_create_spinner_item
        );
        raceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raceSpinner.setAdapter(raceAdapter);
        //find position of chosen race tied to character object
        String chosenRace = character.getRace();
        //get the index for this race
        int racePosition = raceAdapter.getPosition(chosenRace);
        //set position in spinner
        raceSpinner.setSelection(racePosition);
    }

    private void createClassDropdown() {
        //populate spinners with database items (dropdown)
        Spinner classSpinner = binding.classSpinner;
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.class_array,
                R.layout.custom_char_create_spinner_item
        );
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);
        //find position of chosen race tied to character object
        String chosenClass = character.getClazz();
        //get the index for this race
        int classPosition = classAdapter.getPosition(chosenClass);
        //set position in spinner
        classSpinner.setSelection(classPosition);
    }

    //background
    private void createBackgroundDropdown() {
        //populate spinners with database items (dropdown)
        Spinner bgSpinner = binding.bgSpinner;
        ArrayAdapter<CharSequence> bgAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.background,
                R.layout.custom_char_create_spinner_item
        );
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bgSpinner.setAdapter(bgAdapter);
        //find position of chosen race tied to character object
        String chosenBG = character.getBackground();
        //get the index for this race
        int bgPosition = bgAdapter.getPosition(chosenBG);
        //set position in spinner
        bgSpinner.setSelection(bgPosition);
    }
    //character alignment
    private void createCharAlignDropDown() {
        Spinner charAlignSpinner = binding.charAlignSpinner;
        ArrayAdapter<CharSequence> charAlignAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.character_alignment_array,
                R.layout.custom_char_create_spinner_item
        );
        charAlignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        charAlignSpinner.setAdapter(charAlignAdapter);
        String chosenCharAlign = character.getCharacterAlignment();
        int charAlignPosition = charAlignAdapter.getPosition(chosenCharAlign);
        charAlignSpinner.setSelection(charAlignPosition);
    }

    //gender
    private void createGenderDropDown() {
        Spinner genderSpinner = binding.genderSpinner;
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                R.layout.custom_char_create_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        String chosenGender = character.getGender();
        int genderPosition = genderAdapter.getPosition(chosenGender);
        genderSpinner.setSelection(genderPosition);
    }

    //skin color
    private void createSkinColorDropDown() {
        Spinner skinColorSpinner = binding.skinColorSpinner;
        ArrayAdapter<CharSequence> skinColorAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.skin_color_array,
                R.layout.custom_char_create_spinner_item
        );
        skinColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skinColorSpinner.setAdapter(skinColorAdapter);
        String chosenSkinColor = character.getSkinColor();
        int skinPosition = skinColorAdapter.getPosition(chosenSkinColor);
        skinColorSpinner.setSelection(skinPosition);
    }
    //hair color
    private void createHairColorDropDown() {
        Spinner hairColorSpinner = binding.hairColorSpinner;
        ArrayAdapter<CharSequence> hairColorAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.hair_color_array,
                R.layout.custom_char_create_spinner_item
        );
        hairColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hairColorSpinner.setAdapter(hairColorAdapter);
        String chosenHairColor = character.getHairColor();
        int hairPosition = hairColorAdapter.getPosition(chosenHairColor);
        hairColorSpinner.setSelection(hairPosition);
    }
    //eye color
    private void createEyeColorDropDown() {
        Spinner eyeColorSpinner = binding.eyeColorSpinner;
        ArrayAdapter<CharSequence> eyeColorAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.eye_color_array,
                R.layout.custom_char_create_spinner_item
        );
        eyeColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eyeColorSpinner.setAdapter(eyeColorAdapter);
        String chosenEyeColor = character.getEyeColor();
        int eyePosition = eyeColorAdapter.getPosition(chosenEyeColor);
        eyeColorSpinner.setSelection(eyePosition);
    }

    //skin color

    /**
     * Dropdown for multiple item selections
     */
    private void createLanguageDropdown(){
        //Languages language_array
//        List<String> chosenLanguages = character.getLanguagesArray();
        binding.languageTextView.setText(character.getLanguages());

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
        System.out.println("insertCharacterData method");
        mCharacterName = binding.nameEditText.getText().toString();
        String mRace = binding.raceSpinner.getSelectedItem().toString();
        String mClass = binding.classSpinner.getSelectedItem().toString();

        //Can only choose 1-20
        int mStr = Integer.parseInt(binding.strAttrEditTextView.getText().toString());
        int mDex = Integer.parseInt(binding.dexAttrEditTextView.getText().toString());
        int mInt = Integer.parseInt(binding.intAttrEditTextView.getText().toString());
        int mCha = Integer.parseInt(binding.chaAttrEditTextView.getText().toString());
        int mCon = Integer.parseInt(binding.conAttrEditTextView.getText().toString());
        int mWis = Integer.parseInt(binding.wisAttrEditTextView.getText().toString());
        String mBg = binding.bgSpinner.getSelectedItem().toString();
        String mLan = binding.languageTextView.getText().toString();
        String mCharAlign = binding.charAlignSpinner.getSelectedItem().toString();
        String mGender = binding.genderSpinner.getSelectedItem().toString();
        String mHairColor = binding.hairColorSpinner.getSelectedItem().toString();
        String mEyeColor = binding.eyeColorSpinner.getSelectedItem().toString();
        String mSkinColor = binding.skinColorSpinner.getSelectedItem().toString();
        int mAge = Integer.parseInt(binding.ageEditText.getText().toString());
        int mHeight = Integer.parseInt(binding.heightEditText.getText().toString());
        int mWeight = Integer.parseInt(binding.weightEditText.getText().toString());
        String mNotes = binding.notesEditText.getText().toString();

        if (mCharacterName.isEmpty()) {
            Toast.makeText(this, "Please enter a character name", Toast.LENGTH_SHORT).show();
            return;
        }

        // set data in database
        character.setCharacterName(mCharacterName);
        character.setRace(mRace);
        character.setClazz(mClass);
        character.setStrength(mStr);
        character.setDexterity(mDex);
        character.setIntelligence(mInt);
        character.setCharisma(mCha);
        character.setConstitution(mCon);
        character.setWisdom(mWis);
        character.setBackground(mBg);
        character.setLanguages(mLan);
        character.setCharacterAlignment(mCharAlign);
        character.setGender(mGender);
        character.setHairColor(mHairColor);
        character.setEyeColor(mEyeColor);
        character.setSkinColor(mSkinColor);
        character.setAge(mAge);
        character.setHeight(mHeight);
        character.setWeight(mWeight);
        character.setNotes(mNotes);

        repository.insertCharacterSheet(character).blockingAwait();
        System.out.println("insertCharacterData method after character insert:" + character.toString());
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
        intent.putExtra(CHARACTER_EDIT_CHARACTER, newCharacter);
        return intent;
    }

    public static Intent characterEditActivityIntentFactory(Context context, CharacterSheet character) {
        Intent intent = new Intent(context, CharacterCreationActivity.class);
        intent.putExtra(CHARACTER_EDIT_CHARACTER,  character);
        return intent;
    }

}
