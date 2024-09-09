package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //Declare UI components
    private TextView mileageText;               //Display the current mileage
    private Button notificationToggleButton;    //Button to toggle notifications on/off
    private View notificationBar;               //Visual bar that shows notification status (green or red)
    private boolean notificationsOn = true;     //Boolean to track notification status (default: on)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize UI components from layout
        milageText = findViewById(R.id.mileage_Text);
        notificationToggleButton = findViewById(R.id.notification_toggle_button);
        notificationBar = findViewById(R.id.notification_bar);

        //Set up Tutorial Button click listener
        ImageButton tutorialButton = findViewById(R.id.tutorial_button);
        tutorialButton.setOnClickListener(view -> showTutorialDialog());

        //Set up Home Button click listener
    }
}