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
import android.widget.TextView;

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

        Cursor cursor = getContentResolver().query(PetsEntry.CONTENT_URI,projectionQuery,
                null,null,null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            sqlResults = PetsEntry._ID + " - " +
                    PetsEntry.COLUMN_PET_NAME + " - " +
                    PetsEntry.COLUMN_PET_BREED + " - " +
                    PetsEntry.COLUMN_PET_GENDER + " - " +
                    PetsEntry.COLUMN_PET_WEIGHT + "\n";

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                for (int i = 0; i<cursor.getColumnCount();i++){
                    if (i!=(cursor.getColumnCount()-1)){
                        sqlResults += cursor.getString(i) + " - ";
                    }
                    else{
                        sqlResults += cursor.getString(i) + "\n ";
                    }

                }
                cursor.moveToNext();
            }
            displayView.setText(sqlResults);
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
        //Create the information to insert
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetsEntry.COLUMN_PET_NAME,"TOTOCOKY");
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
