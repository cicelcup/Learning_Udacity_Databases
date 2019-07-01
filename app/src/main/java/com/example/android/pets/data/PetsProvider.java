package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.pets.data.PetsContract.*;
import static com.example.android.pets.data.PetsContract.PetsEntry.*;

//Content Provider for the database
public class PetsProvider extends ContentProvider {
    //Database Helper and database definition
    private PetsHelper petsHelper;
    private SQLiteDatabase db;

    //URI Information for decide the case (values are chosen arbitrary)
    private static final int PETS = 100;
    private static final int PETS_ID = 101;
    //URI to identify when there's not match
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //Log Tag
    public static final String LOG_TAG = PetsProvider.class.getSimpleName();


    /* Static initializer. This is run the first time anything is called from this class.
    Create the Uri to check and compare*/
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS, PETS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS + "/#",
                PETS_ID);
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
            case PETS_ID:
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

        switch (match){
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
        return 0;
    }

    //Method to insert data into the database
    private Uri insertPet(Uri uri, ContentValues values) {
        //Enable to write the database
        db = petsHelper.getWritableDatabase();

        //Data Sanity Check
        String name = values.getAsString(COLUMN_PET_NAME);

        //Check if the name is empty
        if (name == null || name.isEmpty()){
            return null;
        }

        Integer gender = values.getAsInteger(COLUMN_PET_GENDER);
        //Check if the gender is valid or not
        if (gender == null || !isValidGender(gender)){
            return null;
        }

        Integer weight = values.getAsInteger(COLUMN_PET_WEIGHT);
        //Check if the weight is valid or not
        if (weight == null || weight <0 ){
            return null;
        }

        long id = db.insert(TABLE_NAME,null,values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Insert the value into the DB
        return ContentUris.withAppendedId(uri,id);

    }
}
