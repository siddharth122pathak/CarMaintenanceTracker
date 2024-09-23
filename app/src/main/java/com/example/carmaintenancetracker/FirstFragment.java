package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.carmaintenancetracker.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.carmaintenancetracker.VehicleDatabaseHelper.*;

public class FirstFragment extends Fragment {

    private static final int ADD_VEHICLE_REQUEST_CODE = 1;
    //View binding for the fragment's layout
    private FragmentFirstBinding binding;
    //Views to handle mileage, notifications, and buttons
    private TextView mileageText;
    private Button notificationToggleButton;
    private View notificationBar;
    private TextView notificationText;
    private boolean notificationsOn = false; //Default state for notifications
    public TextView titleText;
    private ImageView lastUpdatedFlareIcon;
    private TextView lastUpdatedText;
    private long lastUpdatedTimestamp;

    //List to store chicle mileage and names (IDs or names)
    private final List<Integer> vehicleMileage = new ArrayList<>();
    private final List<String> vehicleList = new ArrayList<>();
    private int currentVehicleIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout using binding
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current vehicle data
        outState.putStringArrayList("vehicleList", new ArrayList<>(vehicleList));
        outState.putIntegerArrayList("vehicleMileage", new ArrayList<>(vehicleMileage));
        outState.putInt("currentVehicleIndex", currentVehicleIndex);
    }

    //AddVehicleActivity save button storage
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_VEHICLE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Extract vehicle details from the returned data
            String make = data.getStringExtra("vehicleMake");
            String model = data.getStringExtra("vehicleModel");
            String year = data.getStringExtra("vehicleYear");
            String licensePlate = data.getStringExtra("vehicleLicensePlate");
            String milesStr = data.getStringExtra("vehicleMiles");

            //Parse the mileage string and add it to the vehicleMileage list
            int miles = 0;
            if (milesStr != null && !milesStr.isEmpty()) {
                try {
                    miles = Integer.parseInt(milesStr);
                } catch (NumberFormatException e) {
                    miles = 0; //Handle invalid mileage input
                }
            }

            VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());

            //Add the new vehicle's information to the vehicleList
            vehicleList.add(make + " " + model + " " + year + " " + licensePlate);
            vehicleMileage.add(miles);

            //Ensure the first vehicle remains as the default active vehicle
            if (vehicleList.size() == 1) {
                dbHelper.setActiveVehicle(1);  //Keep the first vehicle as active
                showVehicle(0);  //Display the first vehicle
            }

            //Show the new vehicle but do not change the active vehicle (if it’s not the first one)
            if (vehicleList.size() > 1) {
                updateVehicleButtons();
            }
        }
    }

    @SuppressLint({"SetTextI18n", "Range"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize the UI components with the fragment
        mileageText = view.findViewById(R.id.textView_selected_car_mileage);
        notificationToggleButton = view.findViewById(R.id.btn_selected_car_notifications_setting);
        notificationBar = view.findViewById(R.id.textView_selected_car_notifications_setting);
        notificationText = notificationBar.findViewById(R.id.textView_selected_car_notifications_setting);
        titleText = view.findViewById(R.id.selected_car_title);
        lastUpdatedFlareIcon = view.findViewById(R.id.imageView_mileage_last_updated_late);
        lastUpdatedText = view.findViewById(R.id.textView_selected_car_mileage_last_updated);

        VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
        //Check if any vehicle exists in the database
        Cursor cursor = dbHelper.getAllVehicles();
        vehicleList.clear();  //Ensure list is cleared before populating

        //Setting up default state for Last updated text and icon
        lastUpdatedFlareIcon.setVisibility(View.INVISIBLE);

        if (cursor != null && cursor.getCount() == 0) {
            //No vehicles found in the database, prompt the user to add the first vehicle
            promptAddVehicle();
        } else {
            //Populate vehicleList with the active vehicle and show it
            if (cursor.moveToFirst()) {
                vehicleList.clear();
                do {
                    vehicleList.add(cursor.getString(cursor.getColumnIndex(COLUMN_MAKE)) + " " +
                            cursor.getString(cursor.getColumnIndex(COLUMN_MODEL)) + " " +
                            cursor.getString(cursor.getColumnIndex(COLUMN_YEAR)) + " " +
                            cursor.getString(cursor.getColumnIndex(COLUMN_LICENSE)));
                    int activeVehicleID = cursor.getInt(cursor.getColumnIndex("id"));
                    lastUpdatedTimestamp = dbHelper.getLastUpdated(activeVehicleID);
                    updateLastUpdatedText(lastUpdatedTimestamp);
                } while (cursor.moveToNext());

                //Show the first vehicle immediately if available
                showVehicle(0);
            }
            updateVehicleButtons();
        }

        if (cursor != null) {
            cursor.close();
        }

        //If a saved state exists, restore the state (optional)
        if (savedInstanceState != null) {
            vehicleList.addAll(savedInstanceState.getStringArrayList("vehicleList"));
            vehicleMileage.addAll(savedInstanceState.getIntegerArrayList("vehicleMileage"));
            currentVehicleIndex = savedInstanceState.getInt("currentVehicleIndex");

            //Only update the buttons and show the vehicle if there are vehicles in the list
            if (vehicleList.size() > 0) {
                updateVehicleButtons();
                showVehicle(currentVehicleIndex);
            }
        }

        setupVehicleButtons(view);  //Set up the click listeners for vehicle buttons

        //Set up listeners for buttons in this fragment

        //Update Mileage Button: Triggers the mileage update dialog
        Button updateButton = view.findViewById(R.id.btn_selected_car_mileage_update);
        updateButton.setOnClickListener(v -> showUpdateMileageDialog());

        //Notification Toggle Button: Toggles notification on or off
        notificationToggleButton.setOnClickListener(v -> toggleNotifications());

        //Add New Maintenance Button: Opens the add maintenance screen
        binding.btnAddNewMaintenance.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_addnewmaint)
        );

        //View Upcoming Maintenance Button: Opens the screen showing upcoming maintenance
        Button viewMaintenanceButton = view.findViewById(R.id.btn_view_upcoming_maintenance);
        viewMaintenanceButton.setOnClickListener(v -> viewUpcomingMaintenance());
    }

    //Method to set up vehicle buttons
    private void setupVehicleButtons(View view) {
        Button vehicle1Button = view.findViewById(R.id.btn_vehicle_1);
        Button vehicle2Button = view.findViewById(R.id.btn_vehicle_2);
        Button vehicle3Button = view.findViewById(R.id.btn_vehicle_3);

        // Set up button click listeners to swap vehicles
        vehicle1Button.setOnClickListener(v -> switchOrAddVehicle(1));
        vehicle2Button.setOnClickListener(v -> switchOrAddVehicle(2));
        vehicle3Button.setOnClickListener(v -> switchOrAddVehicle(3));
    }

    //Method to update the last updated text
    @SuppressLint("SetTextI18n")
    private void updateLastUpdatedText(long lastUpdatedTimestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy @ HH:mm a", Locale.getDefault());

        //If no update has been made, use placeholder text
        if (lastUpdatedTimestamp == 0){
            lastUpdatedText.setText("Last updated: never");
            lastUpdatedFlareIcon.setVisibility(View.VISIBLE);
            return;
        }

        Date lastUpdated = new Date(lastUpdatedTimestamp);
        lastUpdatedText.setText("Last updated: " + sdf.format(lastUpdated));

        //Check if 3 months have passed
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastUpdatedTimestamp);
        calendar.add(Calendar.MONTH, 3); //Add 3 months to the last updated date

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()){
            lastUpdatedFlareIcon.setVisibility(View.VISIBLE); //Show the flare icon
        } else {
            lastUpdatedFlareIcon.setVisibility(View.INVISIBLE); //Hide the flare icon
        }
    }

    //Method to show a dialog for updating the mileage
    @SuppressLint("SetTextI18n")
    private void showUpdateMileageDialog() {
        //Check if there is an active vehicle selected
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (vehicleList.isEmpty()) {
            builder.setTitle("No Vehicle Selected");
            builder.setMessage("You need to add a vehicle first before updating mileage.");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        } else {
            //If a vehicle is selected, show the mileage update dialog
            builder.setTitle("Update Mileage");

            //Add input field to dialog
            final EditText input = new EditText(getContext());
            input.setHint("Enter new mileage");
            builder.setView(input);

            //Set up the buttons for the dialog
            builder.setPositiveButton("Update", (dialog, which) -> {
                //Get the new mileage from the input field
                String newMileageStr = input.getText().toString();
                if (!newMileageStr.isEmpty()) {
                    int newMileage = Integer.parseInt(newMileageStr);
                    //Ensure the list is large enough
                    if (vehicleMileage.size() > currentVehicleIndex) {
                        vehicleMileage.set(currentVehicleIndex, newMileage); //Update the mileage for the current vehicle
                    } else {
                        vehicleMileage.add(newMileage); //Add the new mileage if the list is not large enough
                    }

                    mileageText.setText(newMileage + " miles"); //Update the displayed mileage

                    //Update mileage in the database
                    VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
                    Cursor cursor = dbHelper.getActiveVehicle();  //Get the active vehicle
                    if (cursor.moveToFirst()) {
                        @SuppressLint("Range") int activeVehicleId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));  //Get the ID of the active vehicle
                        dbHelper.updateMileage(activeVehicleId, newMileage);  //Update the mileage in the database

                        //Update the last updated timestamp
                        long updatedTimestamp = dbHelper.getLastUpdated(activeVehicleId);
                        updateLastUpdatedText(updatedTimestamp); //Refresh the "Last updated" text and flare icon
                    }
                    cursor.close();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            //Show the mileage update dialog
        }
        builder.show();
    }

    //Method to toggle notifications on/off
    @SuppressLint("SetTextI18n")
    private void toggleNotifications() {
        notificationsOn = !notificationsOn;// Toggle the boolean value
        if (notificationsOn) {
            // Set the button text and bar color when notifications are ON
            notificationToggleButton.setText("Turn Off");
            Drawable greenGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.green_border_gradient);
            notificationBar.setBackground(greenGradient);
            notificationText.setText("Notifications for this vehicle are ON");
        } else {
            // Set the button text and bar color when notifications are OFF
            notificationToggleButton.setText("Turn On");
            Drawable redGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.gray_border_gradient);
            notificationBar.setBackground(redGradient);
            notificationText.setText("Notifications for this vehicle are OFF");
        }
    }

    //Method to handle adding new maintenance
    private void addNewMaintenance() {
    }

    //Method to handle viewing upcoming maintenance
    private void viewUpcomingMaintenance() {
        Intent intent = new Intent(getContext(), UpcomingMaintenanceActivity.class);
        startActivityForResult(intent, 1);
    }

    //Method to display mileage for the selected vehicle
    @SuppressLint({"SetTextI18n", "Range"})
    private void showVehicle(int vehicleIndex) {
        if (vehicleIndex >= 0 && vehicleIndex < vehicleList.size()) {
            //Show the selected vehicle
            String makeModelYear = vehicleList.get(vehicleIndex);
            String[] vehicleDetails = makeModelYear.split(" ");
            String licensePlate = vehicleDetails.length > 3 ? vehicleDetails[3] : "";

            //Check if the license plate is available and non-empty
            if (!licensePlate.isEmpty()) {
                //If license plate is provided, set it as the title
                binding.selectedCarTitle.setText(licensePlate);
            } else {
                //If no license plate, use the year, make, and model
                String yearMakeModel = vehicleDetails[0] + " " + vehicleDetails[1] + " " + vehicleDetails[2];
                binding.selectedCarTitle.setText(yearMakeModel);
            }

            //Retrieve mileage from the database
            VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
            Cursor cursor = dbHelper.getActiveVehicle();
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String milesStr = cursor.getString(cursor.getColumnIndex(COLUMN_MILES));
                int mileage = 0;
                if (milesStr != null && !milesStr.isEmpty()) {
                    try {
                        mileage = Integer.parseInt(milesStr);
                    } catch (NumberFormatException e) {
                        mileage = 0; // Handle invalid mileage
                    }
                }
                if (mileage > 0) {
                    mileageText.setText(mileage + " miles");
                } else {
                    mileageText.setText("Mileage not available");
                }
                //Fetch and display the correct last updated timestamp for the active vehicle
                long lastUpdatedTimestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_UPDATE));
                updateLastUpdatedText(lastUpdatedTimestamp);  //Pass the timestamp to the update method
            }
            cursor.close();
        }
    }

    //Method to switch between existing vehicles or add a new one
    private void switchOrAddVehicle(int index) {
        if (index < vehicleList.size()) {
            switchVehicle(index);
        } else {
            promptAddVehicle();
        }
    }

    //Switch between vehicles based on index
    @SuppressLint("Range")
    private void switchVehicle(int vehicleIndex) {
        if (vehicleIndex >= 1 && vehicleIndex < vehicleList.size()) {
            VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());

            //Swap the data between Vehicle 0 (id 1 in the database) and the selected vehicle (vehicleIndex + 1)
            dbHelper.swapVehicles(1, vehicleIndex + 1);  //Swapping Vehicle 0 and the selected vehicle

            //Set the newly active vehicle
            dbHelper.setActiveVehicle(1);  //Vehicle 0 is now the active vehicle

            //Update UI with the newly active vehicle
            updateActiveVehicleUI();
            updateVehicleButtons();
        }
    }

    @SuppressLint("Range")
    private void updateActiveVehicleUI() {
        VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
        vehicleList.clear();

        //Fetch the newly active vehicle
        Cursor activeVehicleCursor = dbHelper.getActiveVehicle();
        if (activeVehicleCursor != null && activeVehicleCursor.moveToFirst()) {
            String make = activeVehicleCursor.getString(activeVehicleCursor.getColumnIndex(COLUMN_MAKE));
            String model = activeVehicleCursor.getString(activeVehicleCursor.getColumnIndex(COLUMN_MODEL));
            String year = activeVehicleCursor.getString(activeVehicleCursor.getColumnIndex(COLUMN_YEAR));
            String license = activeVehicleCursor.getString(activeVehicleCursor.getColumnIndex(COLUMN_LICENSE));
            String milesStr = activeVehicleCursor.getString(activeVehicleCursor.getColumnIndex(COLUMN_MILES));

            vehicleList.add(make + " " + model + " " + year + " " + license);

            //Update the mileage for the active vehicle
            vehicleMileage.clear();
            int mileage = 0;
            if (milesStr != null && !milesStr.isEmpty()) {
                try {
                    mileage = Integer.parseInt(milesStr);
                } catch (NumberFormatException e) {
                    mileage = 0; // Handle invalid mileage
                }
            }
            vehicleMileage.add(mileage);

            showVehicle(0);  // Show the updated active vehicle
        }

        if (activeVehicleCursor != null) {
            activeVehicleCursor.close();
        }

        updateVehicleButtons();
    }

    //Prompt user to add new vehicle
    private void promptAddVehicle() {
        Intent intent = new Intent(getContext(), AddVehicleActivity.class);
        startActivityForResult(intent, 1);
    }

    //Update the button text dynamically based on the number of vehicles
    @SuppressLint({"Range", "SetTextI18n"})
    private void updateVehicleButtons() {
        Button vehicle1Button = Objects.requireNonNull(getView()).findViewById(R.id.btn_vehicle_1);
        Button vehicle2Button = Objects.requireNonNull(getView()).findViewById(R.id.btn_vehicle_2);
        Button vehicle3Button = Objects.requireNonNull(getView()).findViewById(R.id.btn_vehicle_3);

        VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
        Cursor cursor = dbHelper.getAllVehicles();

        //Clear vehicleList to avoid duplication
        vehicleList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String make = cursor.getString(cursor.getColumnIndex(COLUMN_MAKE));
                String model = cursor.getString(cursor.getColumnIndex(COLUMN_MODEL));
                String year = cursor.getString(cursor.getColumnIndex(COLUMN_YEAR));
                String license = cursor.getString(cursor.getColumnIndex(COLUMN_LICENSE));

                vehicleList.add(make + " " + model + " " + year + " " + license);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Button 1: Assign Vehicle 1 or "Add New Vehicle" if there's only 1 vehicle
        if (vehicleList.size() > 1) {
            vehicle1Button.setText(vehicleList.get(1));  // Second vehicle in the list
        } else {
            vehicle1Button.setText("Add New Vehicle");
        }

        // Button 2: Assign Vehicle 2 or "Add New Vehicle"
        if (vehicleList.size() > 2) {
            vehicle2Button.setText(vehicleList.get(2));  // Third vehicle in the list
        } else {
            vehicle2Button.setText("Add New Vehicle");
        }

        //Button 3: Assign Vehicle 4 or "Add New Vehicle" if there's no fourth vehicle
        if (vehicleList.size() > 3) {
            vehicle3Button.setText(vehicleList.get(3));  //Fourth vehicle in the list
        } else {
            vehicle3Button.setText("Add New Vehicle");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding to avoid memory leaks
        binding = null;
    }
}