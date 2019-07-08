package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;

/** Class for recycler the view of the pets*/
public class PetsCursorAdapter extends CursorAdapter {
    //Constructor of the cursor adapter
    PetsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /*flags*/);
    }

    //Inflate the layout list_item to implement the new view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //Bind the view with the cursor information
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Text View for the name
        TextView petNameView = view.findViewById(R.id.pet_name);

        //Check the index of the column in the table
        int petNameIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);

        //Set the name
        petNameView.setText(cursor.getString(petNameIndex));

        //Text View for the breed
        TextView breedNameView = view.findViewById(R.id.pet_breed);

        //Check the index of the column in the table
        int petBreedIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);

        //Set the breed
        String petBreed = cursor.getString(petBreedIndex);


        //Check if the breed comes null or not
        if (!petBreed.isEmpty()) {
            breedNameView.setText(petBreed);
        } else {
            breedNameView.setText(R.string.unknown_breed);
        }


    }
}
