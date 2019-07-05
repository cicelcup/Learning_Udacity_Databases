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
import static com.example.android.pets.data.PetsContract.PetsEntry.CONTENT_ITEM_TYPE;
import static com.example.android.pets.data.PetsContract.PetsEntry.CONTENT_LIST_TYPE;
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
            //Query the whole table
            case PETS:
                queryCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            //Query a selection
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

            //throw a exception if an option not valid is received
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Set the notification when the data change.
        queryCursor.setNotificationUri(getContext().getContentResolver(),uri);
        //Return the cursor
        return queryCursor;
    }

    //Returns the MIME type of data for the content URI
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            //Return dir type
            case PETS:
                return CONTENT_LIST_TYPE;

            //Return item type
            case PET_ID:
                return CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    //Insert using the content provider
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //Check the URI arrived
        final int match = sUriMatcher.match(uri);

        //Insert the values into the database
        if (match == PETS) {
            return insertPet(uri, values);
        }
        throw new IllegalArgumentException("Insertion is not supported for: " + uri);
    }

    //Method to insert data into the database
    private Uri insertPet(Uri uri, ContentValues values) {
        //Enable to write the database
        db = petsHelper.getWritableDatabase();

        //Check the data
        if (sanityCheck(values)) {
            long id = db.insert(TABLE_NAME, null, values);

            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }

            //Set the notification of the uri
            getContext().getContentResolver().notifyChange(uri,null);
            //Insert the value into the DB
            return ContentUris.withAppendedId(uri, id);
        } else {
            return null;
        }
    }

    /*delete using the content provider*/

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Open the database for writing
        db = petsHelper.getWritableDatabase();

        //Variable to check the uri
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            // Delete all rows that match the selection and selection args
            case PETS:
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;

            //Delete a single row
            case PET_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;

            //Throw an exception
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }

    /*method to update the database*/
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            //update the whole table
            case PETS:
                return updatePet(uri,values, selection, selectionArgs);
            //Update the selection
            case PET_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            //throw an exception
            default:
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);

        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Open the database
        db = petsHelper.getWritableDatabase();

        //Check that every field is set properly
        if (sanityCheck(values)) {
            //Check if the size of values is different than 0
            if (values.size() != 0) {
                //Notify the change
                int rowsUpdate = db.update(TABLE_NAME, values, selection, selectionArgs);

                if(rowsUpdate !=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsUpdate;
            } else {
                return 0;
            }
        } else {
            return 0;
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
        return weight != null && weight >= 0;
    }
}
