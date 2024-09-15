package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.carmaintenancetracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // AppBarConfiguration to handle navigation with a toolbar
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inflate the layout and get a binding instance
        // View binding for the activity's main layout
        com.example.carmaintenancetracker.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); //Set the content to the activity's main view

        //Set up the toolbar as the app's action bar
        setSupportActionBar(binding.toolbar);

        //Set up navigation controller for navigating between fragments
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // AppBarConfiguration to handle navigation with a toolbar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //Link the navigation controller with the toolbar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //Set up the toolbar buttons

        //Set up the ImageView buttons
        ImageView tutorialButton = findViewById(R.id.btn_tutorial);
        ImageView homeButton = findViewById(R.id.imageView_logo);
        ImageView notesButton = findViewById(R.id.btn_notes);

        //Add OnClickListener for each button
        //tutorialButton.setOnClickListener(v -> showTutorial());
        homeButton.setOnClickListener(v -> navigateHome());
        //notesButton.setOnClickListener(v -> openNotes());
    }

    //Method to display the tutorial
   // private void showTutorial(){
        //Logic to show the tutorial screen or overlay
   // }

    //Method to navigate to the home screen
   private void navigateHome(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //Navigate to the MainActivity
        navController.navigate(R.id.FirstFragment);
    }

    //Method to open the notes screen
    //private void openNotes(){
        //Logic to open the notes section
    //}

    //Handles the back/up navigation support for the toolbar
    @Override
    public boolean onSupportNavigateUp() {
        //Get the navigation controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //Handle the "Up" button in the action bar
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}