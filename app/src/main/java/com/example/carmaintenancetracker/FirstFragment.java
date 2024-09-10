package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    //View binding for the fragment's layout
    private FragmentFirstBinding binding;
    //Views to handle mileage, notifications, and buttons
    private TextView mileageText;
    private Button notificationToggleButton;
    private View notificationBar;
    private boolean notificationsOn = true; //Default state for notifications

    //List to store chicle mileage and names (IDs or names)
    private final List<Integer> vehicleMileage = new ArrayList<>();
    private final List<String> vehicleList = new ArrayList<>();
    private int currentVehicleIndex = 0;

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

        //Initialize the vehicle list if empty
        if (vehicleList.isEmpty()) {
            promptAddVehicle();
        } else {
            updateVehicleButtons();
            showVehicle(currentVehicleIndex);
        }
        setupVehicleButtons(view);

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
    }

    //Method to set up vehicle buttons
    private void setupVehicleButtons(View view){

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
    private void showUpdateMileageDialog(){
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
                    vehicleMileage.set(currentVehicleIndex, newMileage); // Update the mileage for the current vehicle
                    mileageText.setText(newMileage + " miles"); // Update the displayed mileage
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
            notificationBar.setBackgroundColor(0xFF00FF00);
        } else {
            // Set the button text and bar color when notifications are OFF
            notificationToggleButton.setText("Turn On");
            notificationBar.setBackgroundColor(0xFFFF0000);
        }
    }

    //Method to handle adding new maintenance
    private void addNewMaintenance() {
        // Logic to open the "Add New Maintenance" screen or fragment
    }

    //Method to handle viewing upcoming maintenance
    private void viewUpcomingMaintenance() {
        //Logic to navigate to the "Upcoming Maintenance" screen or fragment
    }

    //Method to display mileage for the selected vehicle
    @SuppressLint("SetTextI18n")
    private void showVehicle(int vehicleIndex) {
        if (vehicleIndex >= 0 && vehicleIndex < vehicleList.size()) {
            currentVehicleIndex = vehicleIndex;
            mileageText.setText(vehicleList.get(vehicleIndex) + " miles");
        }
    }

    //Method to switch between existing vehicles or add a new one
    private void switchOrAddVehicle(int index) {
        if (index < vehicleList.size()) {
            switchVehicle(index);
        }else {
            promptAddVehicle();
        }
    }

    //Switch between vehicles based on index
    private void switchVehicle(int vehicleIndex) {
        if (vehicleIndex >= 0 && vehicleIndex < vehicleList.size()) {
            currentVehicleIndex = vehicleIndex;
            showVehicle(vehicleIndex);
        }
    }

    //Prompt user to add new vehicle
    private void promptAddVehicle() {
        //Intent intent = new Intent(getContext(), activity_add_vehicle);
        //startActivity(intent);
    }

    //Update the button text dynamically based on the number of vehicles
    private void updateVehicleButtons() {
        Button vehicle1Button = getView().findViewById(R.id.btn_vehicle_1);
        Button vehicle2Button = getView().findViewById(R.id.btn_vehicle_2);
        Button vehicle3Button = getView().findViewById(R.id.btn_vehicle_3);

        if (!vehicleList.isEmpty()) {
            vehicle1Button.setText(vehicleList.get(0));
        }

        if (vehicleList.size() > 1) {
            vehicle2Button.setText(vehicleList.get(1));
        } else {
            vehicle2Button.setText("Add New Vehicle");
        }

        if (vehicleList.size() > 2) {
            vehicle3Button.setText(vehicleList.get(2));
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