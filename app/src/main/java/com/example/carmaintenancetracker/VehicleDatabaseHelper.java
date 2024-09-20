package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
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
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MAKE = "make";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_LICENSE = "license";
    public static final String COLUMN_MILES = "miles";

    //Constructor for creating the SQLiteOpenHelper instance
    public VehicleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Method when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table for storing vehicle data with an "active" column
        String CREATE_VEHICLE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MAKE + " TEXT, "
                + COLUMN_MODEL + " TEXT, "
                + COLUMN_YEAR + " TEXT, "
                + COLUMN_LICENSE + " TEXT, "
                + COLUMN_MILES + " TEXT, "
                + "active INTEGER DEFAULT 0" + ")";
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

    public void setActiveVehicle(int vehicleId) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Reset all vehicles to inactive
        ContentValues resetValues = new ContentValues();
        resetValues.put("active", 0);
        db.update(TABLE_NAME, resetValues, null, null);  //Reset all vehicles

        //Set the selected vehicle as active
        ContentValues values = new ContentValues();
        values.put("active", 1);  // Set active vehicle
        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId)});

        db.close();
    }

    public Cursor getActiveVehicle() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE active = 1 LIMIT 1", null);
    }

    @SuppressLint("Range")
    public void swapVehicles(int vehicleId1, int vehicleId2) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Get the data for vehicle 1
        Cursor cursor1 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{String.valueOf(vehicleId1)});
        Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{String.valueOf(vehicleId2)});

        if (cursor1.moveToFirst() && cursor2.moveToFirst()) {
            //Extract data for vehicle 1 (Vehicle 0)
            ContentValues vehicle1 = new ContentValues();
            vehicle1.put(COLUMN_MAKE, cursor1.getString(cursor1.getColumnIndex(COLUMN_MAKE)));
            vehicle1.put(COLUMN_MODEL, cursor1.getString(cursor1.getColumnIndex(COLUMN_MODEL)));
            vehicle1.put(COLUMN_YEAR, cursor1.getString(cursor1.getColumnIndex(COLUMN_YEAR)));
            vehicle1.put(COLUMN_LICENSE, cursor1.getString(cursor1.getColumnIndex(COLUMN_LICENSE)));
            vehicle1.put(COLUMN_MILES, cursor1.getString(cursor1.getColumnIndex(COLUMN_MILES)));

            //Extract data for vehicle 2 (Selected Vehicle)
            ContentValues vehicle2 = new ContentValues();
            vehicle2.put(COLUMN_MAKE, cursor2.getString(cursor2.getColumnIndex(COLUMN_MAKE)));
            vehicle2.put(COLUMN_MODEL, cursor2.getString(cursor2.getColumnIndex(COLUMN_MODEL)));
            vehicle2.put(COLUMN_YEAR, cursor2.getString(cursor2.getColumnIndex(COLUMN_YEAR)));
            vehicle2.put(COLUMN_LICENSE, cursor2.getString(cursor2.getColumnIndex(COLUMN_LICENSE)));
            vehicle2.put(COLUMN_MILES, cursor2.getString(cursor2.getColumnIndex(COLUMN_MILES)));

            //Swap the data between Vehicle 0 and the selected vehicle
            db.update(TABLE_NAME, vehicle2, COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId1)});  // Update vehicle 0 with vehicle 2 data
            db.update(TABLE_NAME, vehicle1, COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId2)});  // Update selected vehicle with vehicle 0 data
        }

        cursor1.close();
        cursor2.close();
        db.close();
    }
}
