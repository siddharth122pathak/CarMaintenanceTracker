package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.carmaintenancetracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // AppBarConfiguration to handle navigation with a toolbar
    private final AppBarConfiguration appBarConfiguration;

    public MainActivity(AppBarConfiguration appBarConfiguration) {
        this.appBarConfiguration = appBarConfiguration;
    }

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

        //Tutorial Button: Shows the tutorial when clicked
        //ImageButton tutorialButton = findViewById(R.id.tutorial_button);
        //tutorialButton.setOnClickListener(view -> showTutorial());

        //Home Button: Navigates the user back to the home screen
        //ImageButton homeButton = findViewById(R.id.home_Button);
        //homeButton.setOnClickListener(view -> navigateHome());

        //Notes Button: Opens the notes section when clicked
        //ImageButton notesButton = findViewById(R.id.notes_button);
        //notesButton.setOnClickListener(view -> openNotes());
    }

    //Method to display the tutorial
   // private void showTutorial(){
        //Logic to show the tutorial screen or overlay
   // }

    //Method to navigate to the home screen
   // private void navigateHome(){
        //Logic to navigate back to the home page
   // }

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