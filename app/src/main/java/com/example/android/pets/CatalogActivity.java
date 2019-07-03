package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsContract.PetsEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    //String provisional to print on screen
    private String sqlResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout to use in the activity
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //Click event to open the editor
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //Display the info of the database
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        //Select the columns to show
        String[] projectionQuery = {PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED,
                PetsEntry.COLUMN_PET_GENDER,
                PetsEntry.COLUMN_PET_WEIGHT
        };

        //Indicate the field to filter
        String selection = PetsEntry.COLUMN_PET_GENDER +"=?";

        //Indicate the arguments
        String[] selectionArgs = {String.valueOf(PetsEntry.GENDER_FEMALE)};

        //Query the cursor using the content resolver to search the content provider
        Cursor cursor = getContentResolver().query(PetsEntry.CONTENT_URI,projectionQuery,
                null,null,null);

        //Search the listView for the data
        ListView listView = findViewById(R.id.pet_data_list_view);

        //Set the cursor adapter
        PetsCursorAdapter petsCursorAdapter = new PetsCursorAdapter(this,cursor);
        listView.setAdapter(petsCursorAdapter);
    }

    //Created the option menu in the activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    //When it's clicked one item of the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                //Call the insert method of a new pet
                insertPet();
                //Display the information of the database
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet() {
        //Create the information to insert using the content provider
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetsEntry.COLUMN_PET_NAME,"Tululi");
        values.put(PetsContract.PetsEntry.COLUMN_PET_BREED, "Shitzu");
        values.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        values.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT,75);
        getContentResolver().insert(PetsEntry.CONTENT_URI,values);
    }

    @Override
    protected void onStart() {
        super.onStart();
     displayDatabaseInfo();
    }
}
