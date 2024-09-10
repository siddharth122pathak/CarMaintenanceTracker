package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carmaintenancetracker.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    //View binding for the fragment's layout
    private FragmentFirstBinding binding;
    //Views to handle mileage, notifications, and buttons
    private TextView mileageText;
    private Button notificationToggleButton;
    private View notificationBar;
    private boolean notificationsOn = true; //Default state for notifications

    //Sample data for vehicle mileage
    private final int[] vehicleMileage = {101475, 50000, 20000}; //Example for 3 vehicles
    private int currentVehicleIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout using binding
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return the root view of the fragment's layout
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize the UI components with the fragment
        mileageText = view.findViewById(R.id.textView_selected_car_mileage);
        notificationToggleButton = view.findViewById(R.id.btn_selected_car_notifications_setting);
        notificationBar = view.findViewById(R.id.textView_selected_car_notifications_setting);

        //Display current vehicle's mileage
        //Track the current vehicle (0 = Vehicle 1)
        int currentVehicleMileage = 0;
        mileageText.setText(vehicleMileage[currentVehicleMileage] + " miles");

        //Set up listeners for buttons in this fragment

        //Update Mileage Button: Triggers the mileage update dialog
        Button updateButton = view.findViewById(R.id.btn_selected_car_mileage_update);
        updateButton.setOnClickListener(v -> showUpdateMileageDialog());

        //Notification Toggle Button: Toggles notification on or off
        notificationToggleButton.setOnClickListener(v -> toggleNotifications());

        //Add New Maintenance Button: Opens the add maintenance screen
        Button addMaintenanceButton = view.findViewById(R.id.btn_add_new_maintenance);
        addMaintenanceButton.setOnClickListener(v -> addNewMaintenance());

        //View Upcoming Maintenance Button: Opens the screen showing upcoming maintenance
        Button viewMaintenanceButton = view.findViewById(R.id.btn_view_upcoming_maintenance);
        viewMaintenanceButton.setOnClickListener(v -> viewUpcomingMaintenance());

        //Vehicle 1 Button: Switches to Vehicle 1
        Button vehicle1Button = view.findViewById(R.id.btn_vehicle_1);
        vehicle1Button.setOnClickListener(v -> switchVehicle(0));

        //Vehicle 2 Button: Switches to Vehicle 2
        Button vehicle2Button = view.findViewById(R.id.btn_vehicle_2);
        vehicle2Button.setOnClickListener(v -> switchVehicle(1));

        //Vehicle 3 Button: Switches to Vehicle 3
        Button vehicle3Button = view.findViewById(R.id.btn_vehicle_3);
        vehicle3Button.setOnClickListener(v -> switchVehicle(2));
    }

    //Method to show a dialog for updating the mileage
    @SuppressLint("SetTextI18n")
    private void showUpdateMileageDialog() {
        //Create an alert dialog with an input field for mileage
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Mileage");

        //Add input field to dialog
        final EditText input = new EditText(getContext());
        input.setHint("Enter new mileage");
        builder.setView(input);

        //Set up the buttons for the dialog
        builder.setPositiveButton("Update", (dialog, which) -> {
            //Get the new mileage from the input field
            String newMileageStr = input.getText().toString();
            if(!newMileageStr.isEmpty()) {
                int newMileage = Integer.parseInt(newMileageStr);
                vehicleMileage[currentVehicleIndex] = newMileage; //Update the mileage for the current vehicle
                mileageText.setText(newMileage + " miles"); //Update the displayed mileage
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        //show the dialog
        builder.show();
    }

    // Method to toggle notifications on/off
    @SuppressLint("SetTextI18n")
    private void toggleNotifications() {
        notificationsOn = !notificationsOn;// Toggle the boolean value
        if (notificationsOn) {
            // Set the button text and bar color when notifications are ON
            notificationToggleButton.setText("Turn Off");
            notificationBar.setBackgroundColor(0xFF00FF00);
        } else {
            // Set the button text and bar color when notifications are OFF
            notificationToggleButton.setText("Turn On");
            notificationBar.setBackgroundColor(0xFFFF0000);
        }
    }

    // Method to handle adding new maintenance
    private void addNewMaintenance() {
        // Logic to open the "Add New Maintenance" screen or fragment
    }

    // Method to handle viewing upcoming maintenance
    private void viewUpcomingMaintenance() {
        // Logic to navigate to the "Upcoming Maintenance" screen or fragment
    }

    // Method to switch between vehicles based on the vehicle index (1, 2, or 3)
    @SuppressLint("SetTextI18n")
    private void switchVehicle(int vehicleIndex) {
        currentVehicleIndex = vehicleIndex; // Update the index for the selected vehicle
        // Update the displayed mileage for the selected vehicle
        mileageText.setText(vehicleMileage[vehicleIndex] + " miles");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding to avoid memory leaks
        binding = null;
    }
}