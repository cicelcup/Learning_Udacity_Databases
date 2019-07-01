package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.example.android.pets.data.PetsContract.PetsEntry;

public class PetsHelper extends SQLiteOpenHelper {

    //Version of the database
    public static final int DATABASE_VERSION = 1;

    //Name of the data base
    public static final String DATABASE_NAME = "shelter.db";


    //Constructor
    public PetsHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create the Database
    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL Statement to create the database according the Schema
        final String SQL_CREATE_PETS = "CREATE TABLE " +
                PetsEntry.TABLE_NAME + " (" +
                PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL, " +
                PetsEntry.COLUMN_PET_BREED + " TEXT, " +
                PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, " +
                PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PETS);
    }

    //Database is in on version 1, is not need it to implement
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
