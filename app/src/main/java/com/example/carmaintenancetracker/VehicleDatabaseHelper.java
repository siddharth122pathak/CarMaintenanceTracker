//package com.example.carmaintenancetracker;
//
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class VehicleDatabaseHelper extends SQLiteOpenHelper {
//
//    //Define the database name, version, and table structure
//    private static final String DATABASE_NAME = "vehicle.db";
//    private static final int DATABASE_VERSION = 1;
//    public static final String TABLE_NAME = "vehicles";
//    public static final String COLUMN_ID = "id";
//    public static final String COLUMN_MAKE = "make";
//    public static final String COLUMN_MODEL = "model";
//    public static final String COLUMN_YEAR = "year";
//    public static final String COLUMN_LICENSE = "nickname";
//    public static final String COLUMN_MILES = "miles";
//    public static final String COLUMN_LAST_UPDATE = "last_update";
//    public static final String COLUMN_NOTIFICATION_STATUS = "notification_status";
//
//    private UserVehicleApi apiService;
//
//    //Constructor for creating the SQLiteOpenHelper instance
//    public VehicleDatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        // Initialize Retrofit and apiService
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://oriosynology2.ddns.net") //Update with your actual server URL
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        apiService = retrofit.create(UserVehicleApi.class);
//    }
//    //Method when the database is first created
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // Create the table for storing vehicle data with an "active" column
//        String CREATE_VEHICLE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + COLUMN_MAKE + " TEXT, "
//                + COLUMN_MODEL + " TEXT, "
//                + COLUMN_YEAR + " TEXT, "
//                + COLUMN_LICENSE + " TEXT, "
//                + COLUMN_MILES + " TEXT, "
//                + COLUMN_LAST_UPDATE + " INTEGER, " //Store as INTEGER (timestamp)
//                + "active INTEGER DEFAULT 0, "
//                + COLUMN_NOTIFICATION_STATUS + " INTEGER DEFAULT 0, "
//                + "is_synced INTEGER DEFAULT 0" + ")";
//        db.execSQL(CREATE_VEHICLE_TABLE);
//    }
//
//    //Method to update the database version
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        //Drop the old table and create a new one
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//    //Method to add a new vehicle to the database
//    public void addVehicle(String make, String model, String year, String nickname) {
//        ContentValues values = new ContentValues(); //Prepare the data for insertion
//        values.put(COLUMN_MAKE, make);
//        values.put(COLUMN_MODEL, model);
//        values.put(COLUMN_YEAR, year);
//        values.put(COLUMN_LICENSE, nickname);
//        values.put(COLUMN_LAST_UPDATE, System.currentTimeMillis());
//        values.put("is_synced", 0);
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.insert(TABLE_NAME, null, values); //Insert the data into the table
//
//        // After inserting the first vehicle, set it as active
//        if (getAllVehicles().getCount() == 1) {
//            setActiveVehicle(1);
//        }
//
//        db.close();
//
//        // Try syncing with server
//        syncVehicleWithServer(make, model, year, nickname);
//    }
//
//    //Method to retrieve all vehicles from the database
//    public Cursor getAllVehicles() {
//        SQLiteDatabase db = this.getWritableDatabase(); //Get a readable database
//        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null); //Query to get all vehicles
//    }
//
//    public void setActiveVehicle(int vehicleId) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        //Reset all vehicles to inactive
//        ContentValues resetValues = new ContentValues();
//        resetValues.put("active", 0);
//        db.update(TABLE_NAME, resetValues, null, null);  //Reset all vehicles
//
//        //Set the selected vehicle as active
//        ContentValues values = new ContentValues();
//        values.put("active", 1);  // Set active vehicle
//        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId)});
//
//        db.close();
//    }
//
//    public Cursor getActiveVehicle() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE active = 1 LIMIT 1", null);
//    }
//
//    @SuppressLint("Range")
//    public void swapVehicles(int vehicleId1, int vehicleId2) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        //Get the data for vehicle 1
//        Cursor cursor1 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{String.valueOf(vehicleId1)});
//        Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{String.valueOf(vehicleId2)});
//
//        if (cursor1.moveToFirst() && cursor2.moveToFirst()) {
//            //Extract data for vehicle 1 (Vehicle 0)
//            ContentValues vehicle1 = new ContentValues();
//            vehicle1.put(COLUMN_MAKE, cursor1.getString(cursor1.getColumnIndex(COLUMN_MAKE)));
//            vehicle1.put(COLUMN_MODEL, cursor1.getString(cursor1.getColumnIndex(COLUMN_MODEL)));
//            vehicle1.put(COLUMN_YEAR, cursor1.getString(cursor1.getColumnIndex(COLUMN_YEAR)));
//            vehicle1.put(COLUMN_LICENSE, cursor1.getString(cursor1.getColumnIndex(COLUMN_LICENSE)));
//            vehicle1.put(COLUMN_MILES, cursor1.getString(cursor1.getColumnIndex(COLUMN_MILES)));
//            vehicle1.put(COLUMN_LAST_UPDATE, cursor1.getLong(cursor1.getColumnIndex(COLUMN_LAST_UPDATE)));
//            vehicle1.put(COLUMN_NOTIFICATION_STATUS, cursor1.getLong(cursor1.getColumnIndex(COLUMN_NOTIFICATION_STATUS)));
//
//            //Extract data for vehicle 2 (Selected Vehicle)
//            ContentValues vehicle2 = new ContentValues();
//            vehicle2.put(COLUMN_MAKE, cursor2.getString(cursor2.getColumnIndex(COLUMN_MAKE)));
//            vehicle2.put(COLUMN_MODEL, cursor2.getString(cursor2.getColumnIndex(COLUMN_MODEL)));
//            vehicle2.put(COLUMN_YEAR, cursor2.getString(cursor2.getColumnIndex(COLUMN_YEAR)));
//            vehicle2.put(COLUMN_LICENSE, cursor2.getString(cursor2.getColumnIndex(COLUMN_LICENSE)));
//            vehicle2.put(COLUMN_MILES, cursor2.getString(cursor2.getColumnIndex(COLUMN_MILES)));
//            vehicle2.put(COLUMN_LAST_UPDATE, cursor2.getLong(cursor2.getColumnIndex(COLUMN_LAST_UPDATE)));
//            vehicle2.put(COLUMN_NOTIFICATION_STATUS, cursor2.getLong(cursor2.getColumnIndex(COLUMN_NOTIFICATION_STATUS)));
//
//            //Swap the data between Vehicle 0 and the selected vehicle
//            db.update(TABLE_NAME, vehicle2, COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId1)});  // Update vehicle 0 with vehicle 2 data
//            db.update(TABLE_NAME, vehicle1, COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId2)});  // Update selected vehicle with vehicle 0 data
//        }
//
//        cursor1.close();
//        cursor2.close();
//        db.close();
//    }
//
//    //Method to assist to update mileage with the update button
//    public void updateMileage(int vehicleId, int newMileage) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_MILES, String.valueOf(newMileage)); //Update the mileage column
//
//        //Update the last updated timestamp
//        values.put(COLUMN_LAST_UPDATE, System.currentTimeMillis());
//
//        //Update the vehicle with the new mileage where the id matches
//        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId)});
//        db.close();
//    }
//
//    //Method to retrieve last updated for active vehicle
//    @SuppressLint("Range")
//    public long getLastUpdated(int vehicleId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT " + COLUMN_LAST_UPDATE + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId)});
//        long lastUpdated = 0;
//        if (cursor.moveToFirst()) {
//            lastUpdated = cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_UPDATE));
//        }
//        cursor.close();
//        db.close();
//        return lastUpdated;
//    }
//
//    public void updateNotificationSetting(int activeVehicleId, boolean notificationsOn) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_NOTIFICATION_STATUS, notificationsOn ? 1 : 0);
//        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(activeVehicleId)});
//        db.close();
//    }
//
//    //Method to get vehicle details by index
//    public Cursor getVehicleByIndex(int vehicleIndex) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        //Adjusting vehicleIndex + 1 to match database row IDs, or adjust based on your implementation
//        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
//        return db.rawQuery(query, new String[]{String.valueOf(vehicleIndex)});
//    }
//
//    private void syncVehicleWithServer(String make, String model, String year, String nickname) {
//        if (apiService == null) {
//            //Log error or handle gracefully
//            Log.e("VehicleDatabaseHelper", "API service is not initialized");
//            return;
//        }
//
//        //Existing code to sync vehicle
//        apiService.addVehicle(1, make, model, year, nickname).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    markVehicleAsSynced(make, model, year);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("VehicleDatabaseHelper", "Failed to sync with server", t);
//            }
//        });
//    }
//
//    private void markVehicleAsSynced(String make, String model, String year) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("is_synced", 1);
//        db.update(TABLE_NAME, values, COLUMN_MAKE + "=? AND " + COLUMN_MODEL + "=? AND " + COLUMN_YEAR + "=?",
//                new String[]{make, model, year});
//        db.close();
//    }
//
//    @SuppressLint("Range")
//    public void syncUnsyncedVehicles(Context context) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE is_synced = 0", null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                String make = cursor.getString(cursor.getColumnIndex(COLUMN_MAKE));
//                String model = cursor.getString(cursor.getColumnIndex(COLUMN_MODEL));
//                String year = cursor.getString(cursor.getColumnIndex(COLUMN_YEAR));
//                String nickname = cursor.getString(cursor.getColumnIndex(COLUMN_LICENSE));
//
//                syncVehicleWithServer(make, model, year, nickname);  //Retry sync for each unsynced vehicle
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//    }
//}
