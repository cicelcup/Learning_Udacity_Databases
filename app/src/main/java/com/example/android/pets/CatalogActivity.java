package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsContract.PetsEntry;

/**
 * Displays list of pets that were entered and stored in the app. Implements the interface of Loader
 * Manager to open a thread to use for the database
 */
public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    //Constant to define the thread number
    private static int PET_LOADER = 0;

    //Adapter to show the pet information
    PetsCursorAdapter petsCursorAdapter;

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

        //Search the listView for the data
        ListView listView = findViewById(R.id.pet_data_list_view);

        //Set the empty view to the list
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //Set the cursor adapter (null because is the loader who fill the adapter)
        petsCursorAdapter = new PetsCursorAdapter(this,null);
        listView.setAdapter(petsCursorAdapter);

        //define the click event for the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Create the intent to open another activity
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);

                //Create the pet Uri to send it into the intent
                Uri currentPetUri = ContentUris.withAppendedId(PetsEntry.CONTENT_URI,id);

                //add new pet uri into the intent
                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        //Initiate the thread for the database
        getLoaderManager().initLoader(PET_LOADER,null,this);
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
                insertDummyPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyPet() {
        //Create the information to insert using the content provider
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetsEntry.COLUMN_PET_NAME,"Tululi");
        values.put(PetsContract.PetsEntry.COLUMN_PET_BREED, "Shitzu");
        values.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        values.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT,75);
        getContentResolver().insert(PetsEntry.CONTENT_URI,values);
    }

    //Creating the thread for the loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Select the columns to show (only name and breed as it was defined into the adapter)
        String[] projectionQuery = {PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED,
        };

        return new CursorLoader(this, PetsEntry.CONTENT_URI,
                projectionQuery, null, null, null);
    }

    //Method is called when is finished the thread
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Change the cursor with new data from the DB
        petsCursorAdapter.swapCursor(data);
    }

    //Resetting the thread
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        //Empty the cursor
        petsCursorAdapter.swapCursor(null);
    }
}
