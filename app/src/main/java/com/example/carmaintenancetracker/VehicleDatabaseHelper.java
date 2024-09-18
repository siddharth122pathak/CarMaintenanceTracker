package com.example.carmaintenancetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VehicleDatabaseHelper extends SQLiteOpenHelper {

    //Define the database name, version, and table structure
    private static final String DATABASE_NAME = "vehicle.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "vehicles";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MAKE = "make";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_LICENSE = "license";
    private static final String COLUMN_MILES = "miles";

    //Constructor for creating the SQLiteOpenHelper instance
    public VehicleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Method when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create the table for storing vehicle data
        String CREATE_VEHICLE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MAKE + " TEXT, "
                + COLUMN_MODEL + " TEXT, "
                + COLUMN_YEAR + " TEXT, "
                + COLUMN_LICENSE + " TEXT, "
                + COLUMN_MILES + " TEXT" + ")";
        db.execSQL(CREATE_VEHICLE_TABLE);
    }

    //Method to update the database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Method to add a new vehicle to the database
    public void addVehicle(String make, String model, String year, String license, String miles) {
        SQLiteDatabase db = this.getWritableDatabase(); //Get a writable database
        ContentValues values = new ContentValues(); //Prepare the data for insertion
        values.put(COLUMN_MAKE, make);
        values.put(COLUMN_MODEL, model);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_LICENSE, license);
        values.put(COLUMN_MILES, miles);
        db.insert(TABLE_NAME, null, values); //Insert the data into the table
        db.close(); //Close the database connection
    }

    //Method to get the first vehicle from the database (default vehicle)
    public Cursor getFirstVehicle() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC LIMIT 1", null);
    }

    //Method to retrieve all vehicles from the database
    public Cursor getAllVehicles() {
        SQLiteDatabase db = this.getWritableDatabase(); //Get a readable database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null); //Query to get all vehicles
    }
}
