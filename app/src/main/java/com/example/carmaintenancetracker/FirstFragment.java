package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.airbnb.lottie.LottieAnimationView;
import com.example.carmaintenancetracker.databinding.FragmentFirstBinding;

import java.text.SimpleDateFormat;
import java.util.*;

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
    long lastUpdatedTimestamp;
    private ImageView carImageView;
    private LottieAnimationView carAnimationView;

    //Views for the new vehicle buttons and animations
    private FrameLayout vehicle1Button, vehicle2Button, vehicle3Button; //New vehicle buttons
    private LottieAnimationView carAnimation1, carAnimation2, carAnimation3; //Lottie animations for each vehicle
    private TextView carDetails1, carMileage1, carDetails2, carMileage2, carDetails3, carMileage3; //New vehicle details and mileage TextViews
    private ImageButton vehicle1ImageButton, vehicle2ImageButton, vehicle3ImageButton; //Add new vehicle buttons

    //List to store chicle mileage and names (IDs or names)
    private final List<Integer> vehicleMileage = new ArrayList<>();
    private final List<String> vehicleList = new ArrayList<>();
    private final List<String> vehicleMakes = new ArrayList<>();
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
                    //Handle invalid mileage input
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
                updateVehicleButtons();
            }

            //Show the new vehicle but do not change the active vehicle (if it’s not the first one)
            if (vehicleList.size() > 1) {
                updateVehicleButtons();
            }
        }
    }

    @SuppressLint({"SetTextI18n", "Range", "WrongViewCast"})
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
        carImageView = view.findViewById(R.id.imageView_selected_car);
        carAnimationView = view.findViewById(R.id.carAnimation);

        //Initialize new vehicle buttons and animations
        vehicle1ImageButton = view.findViewById(R.id.btn_vehicle_1);
        vehicle2ImageButton = view.findViewById(R.id.btn_vehicle_2);
        vehicle3ImageButton = view.findViewById(R.id.btn_vehicle_3);
        vehicle1Button = view.findViewById(R.id.btn_vehicle_1_text); //Vehicle 2 button
        vehicle2Button = view.findViewById(R.id.btn_vehicle_2_text); //Vehicle 3 button
        vehicle3Button = view.findViewById(R.id.btn_vehicle_3_text); //Vehicle 4 button
        carAnimation1 = view.findViewById(R.id.carAnimation1); //Vehicle 2 animation
        carAnimation2 = view.findViewById(R.id.carAnimation2); //Vehicle 3 animation
        carAnimation3 = view.findViewById(R.id.carAnimation3); //Vehicle 4 animation
        carDetails1 = view.findViewById(R.id.car_details1); //Vehicle 2 details
        carMileage1 = view.findViewById(R.id.car_mileage1); //Vehicle 2 mileage
        carDetails2 = view.findViewById(R.id.car_details2); //Vehicle 3 details
        carMileage2 = view.findViewById(R.id.car_mileage2); //Vehicle 3 mileage
        carDetails3 = view.findViewById(R.id.car_details3); //Vehicle 4 details
        carMileage3 = view.findViewById(R.id.car_mileage3); //Vehicle 4 mileage


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
            assert cursor != null;
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

        cursor.close();

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
        binding.btnAddNewMaintenance.setOnClickListener(v -> {
            if (vehicleList.isEmpty()) {
                showNoVehicleSelectedDialog();
            } else {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_addnewmaint);
            }
        });

        //View Upcoming Maintenance Button: Opens the screen showing upcoming maintenance
        binding.btnViewUpcomingMaintenance.setOnClickListener(v -> {
            if (vehicleList.isEmpty()) {
                showNoVehicleSelectedDialog();
            } else {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_upcomingMaintenanceActivity);
            }
        });

        //Attach long press listener to vehicle buttons and active image
        carAnimationView.setOnLongClickListener(v -> showVehicleOptionsDialog(0));
        vehicle1Button.setOnLongClickListener(v -> showVehicleOptionsDialog(1));
        vehicle2Button.setOnLongClickListener(v -> showVehicleOptionsDialog(2));
        vehicle3Button.setOnLongClickListener(v -> showVehicleOptionsDialog(3));
    }

    //Method to set up vehicle buttons
    private void setupVehicleButtons(View view) {
        ImageButton vehicle1Button = view.findViewById(R.id.btn_vehicle_1);
        ImageButton vehicle2Button = view.findViewById(R.id.btn_vehicle_2);
        ImageButton vehicle3Button = view.findViewById(R.id.btn_vehicle_3);

        // Set up button click listeners to swap vehicles
        vehicle1Button.setOnClickListener(v -> switchOrAddVehicle(1));
        vehicle2Button.setOnClickListener(v -> switchOrAddVehicle(2));
        vehicle3Button.setOnClickListener(v -> switchOrAddVehicle(3));
    }

    //Method to update the last updated text
    @SuppressLint("SetTextI18n")
    private void updateLastUpdatedText(long lastUpdatedTimestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy @ HH:mm", Locale.getDefault());

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
        calendar.add(Calendar.MINUTE, 3); //Add 3 months to the last updated date

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()){
            lastUpdatedFlareIcon.setVisibility(View.VISIBLE); //Show the flare icon
        } else {
            lastUpdatedFlareIcon.setVisibility(View.INVISIBLE); //Hide the flare icon
        }
    }

    //Method to show a dialog for updating the mileage
    @SuppressLint("SetTextI18n")
    private void showUpdateMileageDialog() {
        if (vehicleList.isEmpty()) {
            showNoVehicleSelectedDialog();
        } else {
            //Check if there is an active vehicle selected
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            //If a vehicle is selected, show the mileage update dialog
            builder.setTitle("Update Mileage");

            //Add input field to dialog
            final EditText input = new EditText(getContext());
            input.setHint("Enter new mileage");
            //Set input type to number only
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            //Ready to type on fillable
            input.requestFocus();
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
            builder.show();

            //Pop up keyboard to type
            new Handler().postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }, 200);
        }
    }

    //Method to toggle notifications on/off
    @SuppressLint("SetTextI18n")
    private void toggleNotifications() {
        //Check if there are any vehicles added
        if (vehicleList.isEmpty()) {
            showNoVehicleSelectedDialog();
            return;
        }

        notificationsOn = !notificationsOn;// Toggle the boolean value

        if (notificationsOn) {
            //Set the button text and bar color when notifications are ON
            notificationToggleButton.setText("Turn Off");
            Drawable greenGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.green_border_gradient);
            notificationBar.setBackground(greenGradient);
            notificationText.setText("Notifications for this vehicle are ON");
        } else {
            //Set the button text and bar color when notifications are OFF
            notificationToggleButton.setText("Turn On");
            Drawable redGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.gray_border_gradient);
            notificationBar.setBackground(redGradient);
            notificationText.setText("Notifications for this vehicle are OFF");
        }

        //Update the notification setting in the database
        VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
        Cursor cursor = dbHelper.getActiveVehicle();

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int activeVehicleId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            dbHelper.updateNotificationSetting(activeVehicleId, notificationsOn);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    //Method to display mileage for the selected vehicle
    @SuppressLint({"SetTextI18n", "Range"})
    private void showVehicle(int vehicleIndex) {
        if (vehicleIndex >= 0 && vehicleIndex < vehicleList.size()) {
            //Show the selected vehicle
            String yearMakeModel = vehicleList.get(vehicleIndex);
            String[] vehicleDetails = yearMakeModel.split(" ");
            String licensePlate = vehicleDetails.length > 3 ? vehicleDetails[3] : "";

            //Check if the license plate is available and non-empty
            if (!licensePlate.isEmpty()) {
                //If license plate is provided, set it as the title
                binding.selectedCarTitle.setText(licensePlate);
            } else if (vehicleDetails.length >= 3) {
                //If no license plate, use year, make, and model
                String displayText = vehicleDetails[2] + " " + vehicleDetails[0] + " " + vehicleDetails[1];
                binding.selectedCarTitle.setText(displayText);
            } else {
                //Fallback to just year and make if model is also missing
                binding.selectedCarTitle.setText(yearMakeModel);
            }

            //Retrieve the mileage and notification status for the active vehicle
            VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
            Cursor cursor = dbHelper.getActiveVehicle();

            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String milesStr = cursor.getString(cursor.getColumnIndex(COLUMN_MILES));
                @SuppressLint("Range") int notificationEnabled = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFICATION_STATUS));
                String carMake = cursor.getString(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_MAKE));
                int mileage = 0;
                notificationsOn = (notificationEnabled == 1);

                if (milesStr != null && !milesStr.isEmpty()) {
                    try {
                        mileage = Integer.parseInt(milesStr);
                    } catch (NumberFormatException e) {
                        // Handle invalid mileage
                    }
                }
                if (mileage > 0) {
                    mileageText.setText(mileage + " miles");
                } else {
                    mileageText.setText("Mileage not available");
                }

                //Update the UI based on the notification setting
                if (notificationsOn) {
                    notificationToggleButton.setText("Turn Off");
                    notificationText.setText("Notifications for this vehicle are ON");
                    notificationBar.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.green_border_gradient));
                } else {
                    notificationToggleButton.setText("Turn On");
                    notificationText.setText("Notifications for this vehicle are OFF");
                    notificationBar.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.gray_border_gradient));
                }

                //Fetch and display the correct last updated timestamp for the active vehicle
                long lastUpdatedTimestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_UPDATE));
                updateLastUpdatedText(lastUpdatedTimestamp);  //Pass the timestamp to the update method

                //Get the appropriate animation based on the car make
                int animationResource = getAnimationForMake(carMake);

                //Set and play the animation
                if (animationResource == 0) {
                    //Handle missing animation by showing default image
                    carImageView.setVisibility(View.VISIBLE);
                    carAnimationView.setVisibility(View.GONE);
                } else {
                    //Animation found, display it
                    carImageView.setVisibility(View.GONE);
                    carAnimationView.setVisibility(View.VISIBLE);
                    carAnimationView.setAnimation(animationResource);
                    carAnimationView.playAnimation();
                }
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

    @SuppressLint({"Range", "SetTextI18n"})
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

            //Update the titleText
            if (license != null && !license.isEmpty()) {
                titleText.setText(license); //Show nickname if available
            } else {
                titleText.setText(year + " " + make + " " + model); // Default to year, make, and model
            }

            //Update the mileage for the active vehicle
            vehicleMileage.clear();
            int mileage = 0;
            if (milesStr != null && !milesStr.isEmpty()) {
                try {
                    mileage = Integer.parseInt(milesStr);
                } catch (NumberFormatException e) {
                    // Handle invalid mileage
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
        NavHostFragment.findNavController(this).navigate(R.id.action_FirstFragment_to_AddVehicleActivity);
    }

    //Update the button text dynamically based on the number of vehicles
    @SuppressLint({"Range", "SetTextI18n"})
    private void updateVehicleButtons() {
        VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
        Cursor cursor = dbHelper.getAllVehicles();

        //Clear vehicleList and mileage to avoid duplication
        vehicleList.clear();
        vehicleMileage.clear();
        vehicleMakes.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String make = cursor.getString(cursor.getColumnIndex(COLUMN_MAKE));
                String model = cursor.getString(cursor.getColumnIndex(COLUMN_MODEL));
                String year = cursor.getString(cursor.getColumnIndex(COLUMN_YEAR));
                String license = cursor.getString(cursor.getColumnIndex(COLUMN_LICENSE));
                String mileStr = cursor.getString(cursor.getColumnIndex(COLUMN_MILES));

                //Mileage display
                int mileage = mileStr != null && !mileStr.isEmpty() ? Integer.parseInt(mileStr) : 0;
                vehicleMileage.add(mileage);

                //Create the display text depending on whether the license plate exists
                String vehicleText = (license != null && !license.isEmpty())
                        ? license  //Use license if it's available
                        : year + " " + make + " " + model;  //Otherwise, use year, make, model

                //Add the vehicle text to the vehicleList
                vehicleList.add(vehicleText);
                //Store the make for animation purposes
                vehicleMakes.add(make);
            } while (cursor.moveToNext());
        }

        assert cursor != null;
        cursor.close();

        //Button 1: Assign Vehicle 2 or "Add New Vehicle"
        if (vehicleList.size() > 1 && vehicleMileage.size() > 1) {
            vehicle1ImageButton.setVisibility(View.GONE);
            vehicle1Button.setVisibility(View.VISIBLE);
            carDetails1.setText(vehicleList.get(1));
            carMileage1.setText(vehicleMileage.get(1) + " miles");
            carAnimation1.setVisibility(View.VISIBLE);
            carAnimation1.setAnimation(getAnimationForMake(vehicleMakes.get(1)));
            carAnimation1.playAnimation();
            vehicle1Button.setOnClickListener(v -> switchVehicle(1));  //Switch to vehicle 2
        } else {
            //Show the ImageButton for adding a new vehicle
            vehicle1ImageButton.setVisibility(View.VISIBLE);
            vehicle1Button.setVisibility(View.GONE);  //Hide Button
            vehicle1ImageButton.setOnClickListener(v -> promptAddVehicle());  //Add new vehicle
        }

        //Button 2: Assign Vehicle 3 or "Add New Vehicle"
        if (vehicleList.size() > 2 && vehicleMileage.size() > 2) {
            vehicle2ImageButton.setVisibility(View.GONE);
            vehicle2Button.setVisibility(View.VISIBLE);
            carDetails2.setText(vehicleList.get(2));
            carMileage2.setText(vehicleMileage.get(2) + " miles");
            carAnimation2.setVisibility(View.VISIBLE);
            carAnimation2.setAnimation(getAnimationForMake(vehicleMakes.get(2)));
            carAnimation2.playAnimation();
            vehicle2Button.setOnClickListener(v -> switchVehicle(2));  //Switch to vehicle 3
        } else {
            //Show the ImageButton for adding a new vehicle
            vehicle2ImageButton.setVisibility(View.VISIBLE);
            vehicle2Button.setVisibility(View.GONE);  //Hide Button
            vehicle2ImageButton.setOnClickListener(v -> promptAddVehicle());  //Add new vehicle
        }

        //Button 3: Assign Vehicle 4 or "Add New Vehicle"
        if (vehicleList.size() > 3 && vehicleMileage.size() > 3) {
            vehicle3ImageButton.setVisibility(View.GONE);
            vehicle3Button.setVisibility(View.VISIBLE);
            carDetails3.setText(vehicleList.get(3));
            carMileage3.setText(vehicleMileage.get(3) + " miles");
            carAnimation3.setVisibility(View.VISIBLE);
            carAnimation3.setAnimation(getAnimationForMake(vehicleMakes.get(3)));
            carAnimation3.playAnimation();
            vehicle3Button.setOnClickListener(v -> switchVehicle(3));  //Switch to vehicle 4
        } else {
            //Show the ImageButton for adding a new vehicle
            vehicle3ImageButton.setVisibility(View.VISIBLE);
            vehicle3Button.setVisibility(View.GONE);  //Hide Button
            vehicle3ImageButton.setOnClickListener(v -> promptAddVehicle());  //Add new vehicle
        }
    }

    //Method to display the alert dialog when no vehicle is selected
    private void showNoVehicleSelectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Vehicle Selected");
        builder.setMessage("You need to add a vehicle first before using this feature.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            //Navigate to the Add Vehicle Activity
            Intent intent = new Intent(getContext(), AddVehicleActivity.class);
            startActivityForResult(intent, ADD_VEHICLE_REQUEST_CODE);
        });
        builder.show();
    }

    private int getAnimationForMake(String carMake) {
        switch (carMake.toLowerCase()) {
            case "toyota":
                return R.raw.car_animation_toyota;
            case "honda":
                return R.raw.car_animation_honda;
            case "ford":
                return R.raw.car_animation_ford;
            case "bmw":
                return R.raw.car_animation_bmw;
            case "chevrolet":
                return R.raw.car_animation_chevrolet;
            default:
                return R.raw.car_animation;  //Fallback animation if make doesn't match
        }
    }

    //Method to create dialog box to choice what to do with a vehicle
    private boolean showVehicleOptionsDialog(int vehicleIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select an action");

        //Options: Update Name, Transfer Vehicle, Update Image
        String[] options = {"Update Name", "Update Picture", "Transfer Vehicle", "Delete Vehicle"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: //Update Name
                    showUpdateNameDialog(vehicleIndex);
                    break;
                case 1: //Update Picture (implement later)
                    Toast.makeText(getContext(), "Update Picture feature coming soon", Toast.LENGTH_SHORT).show();
                    break;
                case 2: //Transfer Vehicle (implement later)
                    Toast.makeText(getContext(), "Transfer Vehicle feature coming soon", Toast.LENGTH_SHORT).show();
                    break;
                case 3: //Delete Vehicle (implement later)
                    Toast.makeText(getContext(), "Delete Vehicle feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();

        return true;
    }

    //Method to pop up dialog box to update name
    @SuppressLint("Range")
    private void showUpdateNameDialog(int vehicleIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Name");

        //Add input field for the new name
        final EditText input = new EditText(getContext());
        input.setHint("Enter new name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //Add update button to submit
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString();
            //Retrieve the correct vehicle ID from the database
            VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
            Cursor cursor = dbHelper.getVehicleByIndex(vehicleIndex + 1); //+1 if ID is 1-based
            int vehicleId = 0;

            if (cursor != null && cursor.moveToFirst()) {
                vehicleId = cursor.getInt(cursor.getColumnIndex(VehicleDatabaseHelper.COLUMN_ID));
            }
            if (cursor != null) {
                cursor.close();
            }

            //If the name is left empty, delete the nickname from the database (set it to NULL)
            if (newName.isEmpty()) {
                //Update the vehicle in the database to set nickname to NULL
                ContentValues values = new ContentValues();
                values.putNull(VehicleDatabaseHelper.COLUMN_LICENSE); //Set nickname to NULL
                dbHelper.getWritableDatabase().update(VehicleDatabaseHelper.TABLE_NAME, values, VehicleDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId)});
            } else {
                //Otherwise, update the vehicle with the new name
                updateVehicleNameInDatabase(vehicleId, newName);
            }

            //Update the name in the database and refresh the UI
            updateVehicleNameInDatabase(vehicleId, newName);
            updateActiveVehicleUI(); //Refresh the UI to reflect the updated name
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    //Method to update the vehicle name in the database
    private void updateVehicleNameInDatabase(int vehicleId, String newName) {
        VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());

        //Update the license/nickname column in the database
        ContentValues values = new ContentValues();
        values.put(VehicleDatabaseHelper.COLUMN_LICENSE, newName);

        //Update the correct vehicle by its ID in the database
        dbHelper.getWritableDatabase().update(VehicleDatabaseHelper.TABLE_NAME, values, VehicleDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(vehicleId)});
        dbHelper.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Nullify the binding to avoid memory leaks
        binding = null;
    }
}