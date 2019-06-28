package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PetsContract {
    //Private Constructor for not allowing the creation of a instance of this class
    private PetsContract() { }

    //constants for the content provider
    //Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    //Base content Uri (content// + Content Authority)
    public static final Uri BASE_CONTENT_URI = Uri.parse("content//" + CONTENT_AUTHORITY);
    //Table
    public static final String PATH_PETS = "pets";



    //Inner Class for defining the table name

    public static final class PetsEntry implements BaseColumns {
        /*all the variables define here are the names of the fields, not the fields themselves*/

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);


        //Name of the table for pets
        public final static String TABLE_NAME = "pets";

        //ID of the register
        public final static String _ID = BaseColumns._ID;

        //Name of the pet
        public final static String COLUMN_PET_NAME = "name";

        //Breed of the pet
        public final static String COLUMN_PET_BREED = "breed";

        //Gender of the pet
        public final static String COLUMN_PET_GENDER = "gender";

        //Weight of the pet
        public final static String COLUMN_PET_WEIGHT = "weight";

        //Possibles values of the gender
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

    }
}
