package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MileageCheckWorker extends Worker {

    //Constructor for MileageCheckWorker
    public MileageCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);
    }

    //Method to execute when the worker runs in the background
    @SuppressLint("Range")
    @NonNull
    @Override
    public Result doWork() {
        //Create an instance of the database helper to access vehicle data
        VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getApplicationContext());
        Cursor cursor = dbHelper.getAllVehicles(); //Get all vehicles from the database
        long currentTime = System.currentTimeMillis(); //Get the current time

        //Check if there are any vehicles in the database
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //Get the last mileage update timestamp and notification setting for the vehicle
                @SuppressLint("Range") long lastUpdateTimeStamp = cursor.getLong(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_LAST_UPDATE));
                @SuppressLint("Range") int notificationEnabled = cursor.getInt(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_NOTIFICATION_STATUS));

                //If notifications are enabled and the vehicle hasn't been updated for 3 months trigger a notification
                if (notificationEnabled == 1 && shouldNotify(lastUpdateTimeStamp, currentTime)) {
                    //Get vehicle info to include in the notification
                    @SuppressLint("Range") String vehicleInfo = cursor.getString(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_YEAR)) + " " +
                            cursor.getString(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_MAKE)) + " " +
                            cursor.getString(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_MODEL));

                    //Show the notification for this vehicle
                    NotificationHelper.showNotification(getApplicationContext(), vehicleInfo, cursor.getInt(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_ID)));
                }
            } while (cursor.moveToNext()); //Move to the next vehicle in the list
            cursor.close(); //Close the database cursor
        }
        //Indicate the worker completed successfully
        return Result.success();
    }

    //Method to determine if the notification should be sent
    private boolean shouldNotify(long lastUpdateTimeStamp, long currentTime) {
        Calendar lastUpdateCalendar = Calendar.getInstance();
        lastUpdateCalendar.setTimeInMillis(lastUpdateTimeStamp); //Set the calendar to the last update time
        lastUpdateCalendar.add(Calendar.MINUTE, 3); //Add 3 months to the last update time

        //Return true if 3 months have passed, false otherwise
        return lastUpdateCalendar.getTimeInMillis() <= currentTime;
    }
}
