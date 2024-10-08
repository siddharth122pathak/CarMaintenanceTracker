package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.carmaintenancetracker.R.*;

public class NotificationHelper {

    //Unique ID for the notification channel
    private static final String CHANNEL_ID = "CAR_MAINTENANCE_TRACKER";

    //Method to create notification channel
    @SuppressLint("ObsoleteSdkInt")
    public static void createNotificationChannel(Context context) {
        //Check if the device is running Android 0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Channel name, description, priority level
            CharSequence name = "Car Maintenance Notification";
            String description = "Notifications for vehicles requiring updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Method to build and display the notification
    @SuppressLint("MissingPermission")
    public static void showNotification(Context context, String vehicleInfo, int notificationId) {
        //Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) //Icon for the notification
                .setContentTitle("Mileage Update Required") //Title of the notification
                .setContentText("Your vehicle " + vehicleInfo + " requires the mileage to be updated.") //Notification message
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //Notification priority

        //Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}