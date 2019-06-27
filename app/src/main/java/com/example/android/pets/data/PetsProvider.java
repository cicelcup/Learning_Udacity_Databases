package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

//Content Provider for the database
public class PetsProvider extends ContentProvider {
    //Database Helper
    private PetsHelper petsHelper;

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
        return null;
    }

    //Returns the MIME type of data for the content URI
    @Override
    public String getType(Uri uri) {
        return null;
    }

    //Insert using the content provider
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
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
}
