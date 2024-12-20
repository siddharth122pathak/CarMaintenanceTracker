package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.carmaintenancetracker.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // AppBarConfiguration to handle navigation with a toolbar
    private final ThreadLocal<AppBarConfiguration> appBarConfiguration = new ThreadLocal<>();
    public String upcomingMaintenanceMiles;
    public String upcomingMaintenanceTime;

    @SuppressLint("ObsoleteSdkInt")
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
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Check if the destination is AddVehicleActivity, addnewmaint, or UpcomingMaintenanceActivity
            if (destination.getId() == R.id.AddVehicleActivity ||
                    destination.getId() == R.id.addnewmaint ||
                    destination.getId() == R.id.upcomingMaintenanceActivity) {

                // Hide specific buttons in the toolbar
                binding.btnTutorial.setVisibility(View.GONE);
                binding.textViewTutorial.setVisibility(View.GONE);
                binding.btnNotes.setVisibility(View.GONE);
                binding.textViewNotes.setVisibility(View.GONE);

            } else {
                // Show buttons on other pages
                binding.btnTutorial.setVisibility(View.VISIBLE);
                binding.textViewTutorial.setVisibility(View.VISIBLE);
                binding.btnNotes.setVisibility(View.VISIBLE);
                binding.textViewNotes.setVisibility(View.VISIBLE);
            }
        });

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
        notesButton.setOnClickListener(v -> openNotes());

        //Create the notification channel
        NotificationHelper.createNotificationChannel(this);

//       //Schedule periodic work for mileage check (runs every 24 hours)
//        PeriodicWorkRequest mileageCheckWorkRequest = new PeriodicWorkRequest.Builder(MileageCheckWorker.class, 24, TimeUnit.HOURS).build();
//
//        //Enqueue the work request
//        WorkManager.getInstance(this).enqueue(mileageCheckWorkRequest);
//        // Check and request notification permission on Android 13+ (API level 33 and above)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
//            }
//        }
    }

    //Method to display the tutorial
    // private void showTutorial(){
    //Logic to show the tutorial screen or overlay
    // }

    //Method to navigate to the home screen
    private void navigateHome() {
        //Navigate to the MainActivity
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Check if the current destination is already FirstFragment
        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.FirstFragment) {
            navController.navigate(R.id.FirstFragment); // Only navigate if it's not already there
        }
    }

    //Method to open the notes screen
    private void openNotes(){
        //Logic to open the notes section
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.notes_Activity) {
            navController.navigate(R.id.notes_Activity);
        }
    }

    //Handles the back/up navigation support for the toolbar
    @Override
    public boolean onSupportNavigateUp() {
        //Get the navigation controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //Handle the "Up" button in the action bar
        return NavigationUI.navigateUp(navController, Objects.requireNonNull(appBarConfiguration.get())) || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save data if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore data if needed
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now show notifications
            } else {
                // Permission denied, you may want to disable notification-related functionality
            }
        }
    }
}