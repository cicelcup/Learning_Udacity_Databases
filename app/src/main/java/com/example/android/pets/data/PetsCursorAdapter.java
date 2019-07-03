package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.R;

public class PetsCursorAdapter extends CursorAdapter {
    //Constructor of the cursor adapter
    public PetsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /*flags*/);
    }

    //Inflate the layout to implement the new view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //Bind the view with the cursor information
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Text View for the name
        TextView petNameView = view.findViewById(R.id.pet_name);
        int petNameIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);
        petNameView.setText(cursor.getString(petNameIndex));

        //Text View for the breed
        TextView breedNameView = view.findViewById(R.id.pet_breed);
        int petBreedIndex = cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);
        String petBreed = cursor.getColumnName(petBreedIndex);

        //Check if the breed comes null or not
        if (petBreed != null) {
            breedNameView.setText(petBreed);
        } else {
            breedNameView.setText(R.string.unknown_breed);
        }


    }
}
