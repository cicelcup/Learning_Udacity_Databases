package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract.PetsEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /*constant to call the loader */
    private static final int EXISTING_PET_LOADER = 0;

    /* EditText field to enter the pet's name */
    private EditText mNameEditText;

    /* EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /* EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /* EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetsEntry.GENDER_UNKNOWN;

    //Uri to get the which item was pressed
    Uri currentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout for the activity
        setContentView(R.layout.activity_editor);

        //Getting the intent
        Intent intent = getIntent();

        //Getting the data from the intent
        currentUri = intent.getData();

        //Checking what title to set

        if (currentUri == null) {
            //Open from the new pet button
            setTitle(R.string.editor_activity_title_new_pet);
        } else {
            //Open from the item click
            setTitle(R.string.editor_activity_title_edit_pet);

            //Open the thread
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);

        setupSpinner(); //function to set up the spinner
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        //Listen the click of the an item in the Spinner
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetsEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if (savePetDB()) {
                    finish();
                }
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean savePetDB() {
        //Create the information to insert from the values edited
        ContentValues values = new ContentValues();

        //pet Name
        String petName = mNameEditText.getText().toString().trim();
        values.put(PetsEntry.COLUMN_PET_NAME, petName);

        //pet Breed
        String petBreed = mBreedEditText.getText().toString().trim();
        values.put(PetsEntry.COLUMN_PET_BREED, petBreed);

        //pet Gender
        values.put(PetsEntry.COLUMN_PET_GENDER, mGender);

        //pet Weight
        String petWeight = mWeightEditText.getText().toString().trim();

        if (!petWeight.isEmpty()) {
            values.put(PetsEntry.COLUMN_PET_WEIGHT, Integer.parseInt(petWeight));
        } else {
            Toast.makeText(this, "Weight cannot be null", Toast.LENGTH_SHORT).show();
        }

        if (currentUri == null){
            //Insert the value into the DB using the context provider
            Uri uri = getContentResolver().insert(PetsEntry.CONTENT_URI, values);

            if (uri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        else{
            int rowsAffected = getContentResolver().update(
                    currentUri, values, null, null);

            if(rowsAffected == 0){
                Toast.makeText(this, getString(R.string.editor_save_pet_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            else{
                Toast.makeText(this, getString(R.string.editor_save_pet_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Select the columns to show
        String[] projectionQuery = {PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED,
                PetsEntry.COLUMN_PET_GENDER,
                PetsEntry.COLUMN_PET_WEIGHT
        };

        //Indicate the field to filter
        String selection = PetsEntry.COLUMN_PET_GENDER + "=?";

        //Indicate the arguments
        String[] selectionArgs = {String.valueOf(PetsEntry.GENDER_FEMALE)};

        return new CursorLoader(this, currentUri,
                projectionQuery, null, null, null);

    }

    //After the thread is finished
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            //No result
            return;
        }

        if (data.moveToFirst()) {
            //Check the columns for the whole items of the table
            int nameColumnIndex = data.getColumnIndex(PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = data.getColumnIndex(PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = data.getColumnIndex(PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = data.getColumnIndex(PetsEntry.COLUMN_PET_WEIGHT);

            //Set the values of the fields
            mNameEditText.setText(data.getString(nameColumnIndex));
            mBreedEditText.setText(data.getString(breedColumnIndex));
            mWeightEditText.setText(String.valueOf(data.getInt(weightColumnIndex)));
            mGender = data.getInt(genderColumnIndex);

            switch (mGender) {
                case PetsEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetsEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    //Reset the thread
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }
}