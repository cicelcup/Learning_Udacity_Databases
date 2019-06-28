package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

//Content Provider for the database
public class PetsProvider extends ContentProvider {
    //Database Helper
    private PetsHelper petsHelper;
    private SQLiteDatabase db;

    //URI Information for decide the case
    private static final int PETS = 100;
    private static final int PETS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS + "/#",
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
                queryCursor = db.query(PetsContract.PetsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PETS_ID:
                //Select the id
                selection = PetsContract.PetsEntry._ID + "=?";
                // Select value to filter
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                //do the query
                queryCursor = db.query(PetsContract.PetsEntry.TABLE_NAME,
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

    private Uri insertPet(Uri uri, ContentValues values) {
        //Enable to write the database
        db = petsHelper.getWritableDatabase();
        long id = db.insert(PetsContract.PetsEntry.TABLE_NAME,null,values);

        //Insert the value into the DB
        return ContentUris.withAppendedId(uri,id);

    }
}
