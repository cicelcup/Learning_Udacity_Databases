package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetsContract {
    //Private Constructor for not allowing the creation of a instance of this class
    private PetsContract() { }

    //Inner Class for defining the table name

    public static final class PetsEntry implements BaseColumns {
        /*all the variables define here are the names of the fields, not the fields themselves*/

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
