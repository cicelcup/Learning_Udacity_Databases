package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.pets.data.PetsContract.CONTENT_AUTHORITY;
import static com.example.android.pets.data.PetsContract.PATH_PETS;
import static com.example.android.pets.data.PetsContract.PetsEntry.COLUMN_PET_GENDER;
import static com.example.android.pets.data.PetsContract.PetsEntry.COLUMN_PET_NAME;
import static com.example.android.pets.data.PetsContract.PetsEntry.COLUMN_PET_WEIGHT;
import static com.example.android.pets.data.PetsContract.PetsEntry.TABLE_NAME;
import static com.example.android.pets.data.PetsContract.PetsEntry._ID;
import static com.example.android.pets.data.PetsContract.PetsEntry.isValidGender;

//Content Provider for the database
public class PetsProvider extends ContentProvider {
    //Database Helper and database definition
    private PetsHelper petsHelper;
    private SQLiteDatabase db;

    //URI Information for decide the case (values are chosen arbitrary)
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    //URI to identify when there's not match
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //Log Tag
    public static final String LOG_TAG = PetsProvider.class.getSimpleName();


    /* Static initializer. This is run the first time anything is called from this class.
    Create the Uri to check and compare*/
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS, PETS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS + "/#",
                PET_ID);
    }

    //Initialize the content provider
    @Override
    public boolean onCreate() {
        petsHelper = new PetsHelper(getContext());
        return true;
    }

    //Query using the content provider
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        //Enable the DB
        db = petsHelper.getReadableDatabase();

        //Open the cursor
        Cursor queryCursor;

        //Check the URI arrived
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                queryCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PET_ID:
                //Select the id
                selection = _ID + "=?";
                // Select value to filter
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                //do the query
                queryCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        return queryCursor;
    }

    //Returns the MIME type of data for the content URI
    @Override
    public String getType(Uri uri) {
        return null;
    }

    //Insert using the content provider
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //Check the URI arrived
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return insertPet(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }
    }

    //delete using the content provider
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    //update using the content provider
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            //update the whole table
            case PETS:
                return updatePet(values, selection, selectionArgs);
            //Update the selection
            case PET_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(values, selection, selectionArgs);
            //throw an exception
            default:
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);

        }
    }

    //Method to update the database
    private int updatePet(ContentValues values, String selection, String[] selectionArgs) {

        //Open the database
        db = petsHelper.getWritableDatabase();

        //Check that every field is set properly
        if (sanityCheck(values) == true) {
            //Check if the size of values is different than 0
            if (values.size() != 0) {
                return db.update(TABLE_NAME, values, selection, selectionArgs);
            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

    //Method to insert data into the database
    private Uri insertPet(Uri uri, ContentValues values) {
        //Enable to write the database
        db = petsHelper.getWritableDatabase();

        //Check the data
        if (sanityCheck(values) == true) {
            long id = db.insert(TABLE_NAME, null, values);

            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }
            //Insert the value into the DB
            return ContentUris.withAppendedId(uri, id);
        } else {
            return null;
        }
    }

    //Check if the data is setting corrected it
    private boolean sanityCheck(ContentValues values) {

        String name = values.getAsString(COLUMN_PET_NAME);

        //Check if the name is empty
        if (name == null || name.isEmpty()) {
            return false;
        }

        Integer gender = values.getAsInteger(COLUMN_PET_GENDER);
        //Check if the gender is valid or not
        if (gender == null || !isValidGender(gender)) {
            return false;
        }

        Integer weight = values.getAsInteger(COLUMN_PET_WEIGHT);
        //Check if the weight is valid or not
        if (weight == null || weight < 0) {
            return false;
        }
        return true;
    }
}
