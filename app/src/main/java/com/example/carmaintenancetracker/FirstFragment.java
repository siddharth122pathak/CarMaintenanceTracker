package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @noinspection CallToPrintStackTrace*/
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private TextView mileageText;
    private Button notificationToggleButton;
    private View notificationBar;
    private TextView notificationText;
    private boolean notificationsOn = false;
    public TextView titleText;
    private ImageView lastUpdatedFlareIcon;
    private TextView lastUpdatedText;
    private ImageView carImageView;
    private LottieAnimationView carAnimationView;
    private FrameLayout vehicle1Button, vehicle2Button, vehicle3Button;
    private LottieAnimationView carAnimation1, carAnimation2, carAnimation3;
    private TextView carDetails1, carMileage1, carDetails2, carMileage2, carDetails3, carMileage3;
    private ImageButton vehicle1ImageButton, vehicle2ImageButton, vehicle3ImageButton;
    private boolean hasNavigatedToAddVehicle = false;
    private TextView nextMaintenanceTitle;
    private TextView nextMaintenanceMiles;
    private TextView nextMaintenanceDate;

    private final List<Integer> vehicleMileage = new ArrayList<>();
    private final List<String> vehicleList = new ArrayList<>();
    private final List<String> vehicleMakes = new ArrayList<>();
    private final List<UserVehicle> userVehicles = new ArrayList<>(); // Store UserVehicle objects

    private int currentVehicleIndex = 0;

    private UserVehicleApi userVehicleApi;  // Retrofit API interface
    private FortuneApi fortuneApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        // Initialize Retrofit and API
        userVehicleApi = RetrofitClient.getRetrofitInstance().create(UserVehicleApi.class);
        fortuneApi = RetrofitClient.getRetrofitInstance().create(FortuneApi.class);

        loadVehiclesFromServer();
        return binding.getRoot();
    }

    private void loadVehiclesFromServer() {
        String userId = getUserIdFromSession();

        userVehicleApi.getAllVehicles(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        List<UserVehicle> vehicles = parseAllVehicles(jsonResponse);

                        userVehicles.clear();
                        if (vehicles != null) {
                            userVehicles.addAll(vehicles);
                        }

                        if (userVehicles.isEmpty()) {
                            promptAddVehicle();
                        } else {
                            updateVehicleButtons(); // Update UI elements
                            showVehicle(0); // Display the first vehicle by default
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to load vehicle data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error loading vehicles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load vehicles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Retrieve user ID from shared preferences
    public String getUserIdFromSession() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    @SuppressLint({"SetTextI18n", "Range", "WrongViewCast"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        mileageText = view.findViewById(R.id.textView_selected_car_mileage);
        notificationToggleButton = view.findViewById(R.id.btn_selected_car_notifications_setting);
        notificationBar = view.findViewById(R.id.textView_selected_car_notifications_setting);
        notificationText = notificationBar.findViewById(R.id.textView_selected_car_notifications_setting);
        titleText = view.findViewById(R.id.selected_car_title);
        lastUpdatedFlareIcon = view.findViewById(R.id.imageView_mileage_last_updated_late);
        lastUpdatedText = view.findViewById(R.id.textView_selected_car_mileage_last_updated);
        carImageView = view.findViewById(R.id.imageView_selected_car);
        carAnimationView = view.findViewById(R.id.carAnimation);
        nextMaintenanceTitle = view.findViewById(R.id.textView_next_maintenance_title);
        nextMaintenanceMiles = view.findViewById(R.id.textView_next_maintenance_miles);
        nextMaintenanceDate = view.findViewById(R.id.textView_next_maintenance_date);

        // Initialize new vehicle buttons and animations
        vehicle1ImageButton = view.findViewById(R.id.btn_vehicle_1);
        vehicle2ImageButton = view.findViewById(R.id.btn_vehicle_2);
        vehicle3ImageButton = view.findViewById(R.id.btn_vehicle_3);
        vehicle1Button = view.findViewById(R.id.btn_vehicle_1_text);
        vehicle2Button = view.findViewById(R.id.btn_vehicle_2_text);
        vehicle3Button = view.findViewById(R.id.btn_vehicle_3_text);
        carAnimation1 = view.findViewById(R.id.carAnimation1);
        carAnimation2 = view.findViewById(R.id.carAnimation2);
        carAnimation3 = view.findViewById(R.id.carAnimation3);
        carDetails1 = view.findViewById(R.id.car_details1);
        carMileage1 = view.findViewById(R.id.car_mileage1);
        carDetails2 = view.findViewById(R.id.car_details2);
        carMileage2 = view.findViewById(R.id.car_mileage2);
        carDetails3 = view.findViewById(R.id.car_details3);
        carMileage3 = view.findViewById(R.id.car_mileage3);

        // Initialize Retrofit API client
        userVehicleApi = RetrofitClient.getRetrofitInstance().create(UserVehicleApi.class);

        // Default state for last updated text and icon
        lastUpdatedFlareIcon.setVisibility(View.INVISIBLE);
        mileageText.setText("Loading mileage...");

        // Fetch vehicles from server instead of local database
        loadVehiclesFromServer();

        String currentVehicleId = getCarId(currentVehicleIndex);

        // Restore saved state if available
        if (savedInstanceState != null) {
            vehicleList.addAll(savedInstanceState.getStringArrayList("vehicleList"));
            vehicleMileage.addAll(savedInstanceState.getIntegerArrayList("vehicleMileage"));
            currentVehicleIndex = savedInstanceState.getInt("currentVehicleIndex");

            if (!vehicleList.isEmpty()) {
                updateVehicleButtons();
                showVehicle(currentVehicleIndex);
            }
        }

        setupVehicleButtons(view);

        // Update Mileage Button
        Button updateButton = view.findViewById(R.id.btn_selected_car_mileage_update);
        updateButton.setOnClickListener(v -> showUpdateMileageDialog());

        // Load notification state from SharedPreferences
        notificationsOn = loadNotificationPreference(currentVehicleId);

        // Set UI based on the saved state
        if (notificationsOn) {
            notificationToggleButton.setText("Turn Off");
            Drawable greenGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.green_border_gradient);
            notificationBar.setBackground(greenGradient);
            notificationText.setText("Notifications for this vehicle are ON");
        } else {
            notificationToggleButton.setText("Turn On");
            Drawable grayGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.gray_border_gradient);
            notificationBar.setBackground(grayGradient);
            notificationText.setText("Notifications for this vehicle are OFF");
        }

        // Notification Toggle Button
        notificationToggleButton.setOnClickListener(v -> toggleNotifications());

        // Add New Maintenance Button
        binding.btnAddNewMaintenance.setOnClickListener(v -> {
            if (vehicleList.isEmpty()) {
                showNoVehicleSelectedDialog();
            } else {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_addnewmaint);
            }
        });

        // View Upcoming Maintenance Button
        binding.btnViewUpcomingMaintenance.setOnClickListener(v -> {
            if (vehicleList.isEmpty()) {
                showNoVehicleSelectedDialog();
            } else {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_upcomingMaintenanceActivity);
            }
        });

        // Long press listeners for vehicle buttons and active image
        carAnimationView.setOnLongClickListener(v -> showVehicleOptionsDialog(0));
        vehicle1Button.setOnLongClickListener(v -> showVehicleOptionsDialog(1));
        vehicle2Button.setOnLongClickListener(v -> showVehicleOptionsDialog(2));
        vehicle3Button.setOnLongClickListener(v -> showVehicleOptionsDialog(3));
    }


    private boolean loadNotificationPreference(String vehicleId) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("CarMaintenancePrefs", Context.MODE_PRIVATE);
        // Use vehicleId to retrieve the unique key
        return sharedPreferences.getBoolean("notificationsOn_" + vehicleId, false); // Default is false if not set
    }

    // Method to set up vehicle buttons
    private void setupVehicleButtons(View view) {
        vehicle1ImageButton = view.findViewById(R.id.btn_vehicle_1);
        vehicle2ImageButton = view.findViewById(R.id.btn_vehicle_2);
        vehicle3ImageButton = view.findViewById(R.id.btn_vehicle_3);
        vehicle1Button = view.findViewById(R.id.btn_vehicle_1_text);
        vehicle2Button = view.findViewById(R.id.btn_vehicle_2_text);
        vehicle3Button = view.findViewById(R.id.btn_vehicle_3_text);

        // Set up button click listeners to swap or add vehicles
        vehicle1ImageButton.setOnClickListener(v -> promptAddVehicle());
        vehicle2ImageButton.setOnClickListener(v -> promptAddVehicle());
        vehicle3ImageButton.setOnClickListener(v -> promptAddVehicle());
        vehicle1Button.setOnClickListener(v -> switchVehicle(1));
        vehicle2Button.setOnClickListener(v -> switchVehicle(2));
        vehicle3Button.setOnClickListener(v -> switchVehicle(3));
    }


    // Method to update the last updated text
    @SuppressLint("SetTextI18n")
    private void updateLastUpdatedText(Date lastUpdatedTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy @ HH:mm", Locale.getDefault());

        // If no update has been made, use placeholder text
        if (lastUpdatedTimestamp == null) {
            lastUpdatedText.setText("Last updated: never");
            lastUpdatedFlareIcon.setVisibility(View.VISIBLE);
            return;
        }

        lastUpdatedText.setText("Last updated: " + sdf.format(lastUpdatedTimestamp));

        // Check if 3 months have passed since the last update
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastUpdatedTimestamp); // Use Date object directly
        calendar.add(Calendar.MONTH, 3); // Add 3 months to the last updated date

        // Show or hide the flare icon based on the time elapsed
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            lastUpdatedFlareIcon.setVisibility(View.VISIBLE); // Show the flare icon if outdated
        } else {
            lastUpdatedFlareIcon.setVisibility(View.INVISIBLE); // Hide the flare icon if up-to-date
        }
    }

    // Method to show a dialog for updating the mileage
    @SuppressLint("SetTextI18n")
    private void showUpdateMileageDialog() {
        if (vehicleList.isEmpty()) {
            showNoVehicleSelectedDialog();
        } else {
            // Check if there is an active vehicle selected
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Update Mileage");

            // Add input field to dialog
            final EditText input = new EditText(getContext());
            input.setHint("Enter new mileage");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.requestFocus();
            builder.setView(input);

            // Set up the dialog buttons
            builder.setPositiveButton("Update", (dialog, which) -> {
                String newMileageStr = input.getText().toString();
                if (!newMileageStr.isEmpty()) {
                    int newMileage = Integer.parseInt(newMileageStr);

                    // Ensure the list can hold the new mileage for the current vehicle
                    if (vehicleMileage.size() > currentVehicleIndex) {
                        vehicleMileage.set(currentVehicleIndex, newMileage);
                    } else {
                        vehicleMileage.add(newMileage);
                    }

                    // Update displayed mileage
                    mileageText.setText(newMileage + " miles");

                    // Get the active vehicle's ID and update mileage on the server
                    String carId = getCarId(currentVehicleIndex);
                    userVehicleApi.updateMileage(carId, newMileage).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Mileage updated successfully", Toast.LENGTH_SHORT).show();
                                fetchAndUpdateLastUpdated(carId); // Update the "Last updated" text and icon
                            } else {
                                Toast.makeText(getContext(), "Failed to update mileage", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                            Toast.makeText(getContext(), "Error updating mileage", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();

            // Show the keyboard for input
            new Handler().postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }, 200);
        }
    }

    // Fetch and update the last updated timestamp
    private void fetchAndUpdateLastUpdated(String carId) {
        userVehicleApi.getLastUpdated(carId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Read and parse the response safely
                        String responseBody = response.body().string();
                        Log.d("fetchAndUpdateLastUpdated", "Response Body: " + responseBody);

                        // Check for an error in the response
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("status") && jsonObject.getString("status").equals("error")) {
                            Log.e("fetchAndUpdateLastUpdated", "API error: " + jsonObject.getString("message"));
                            Toast.makeText(getContext(), "Error: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Proceed to parse last updated timestamp
                        Date updatedTimestamp = parseLastUpdatedTime(responseBody);
                        if (updatedTimestamp != null) {
                            updateLastUpdatedText(updatedTimestamp); // Refresh the "Last updated" text and icon
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error reading response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve last updated time", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch last updated time", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to toggle notifications on/off
    @SuppressLint("SetTextI18n")
    private void toggleNotifications() {
        notificationsOn = !notificationsOn; // Toggle the notification state

        // Get the current vehicle ID
        String currentVehicleId = getCarId(currentVehicleIndex); // Assume you have a method to get current vehicle ID

        // Save the notification state for the current vehicle
        saveNotificationPreference(currentVehicleId, notificationsOn);

        // Update the UI based on the new state
        if (notificationsOn) {
            notificationToggleButton.setText("Turn Off");
            Drawable greenGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.green_border_gradient);
            notificationBar.setBackground(greenGradient);
            notificationText.setText("Notifications for this vehicle are ON");
        } else {
            notificationToggleButton.setText("Turn On");
            Drawable grayGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.gray_border_gradient);
            notificationBar.setBackground(grayGradient);
            notificationText.setText("Notifications for this vehicle are OFF");
        }
    }

    private void saveNotificationPreference(String vehicleId, boolean notificationsOn) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("CarMaintenancePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Use vehicleId to create a unique key
        editor.putBoolean("notificationsOn_" + vehicleId, notificationsOn);
        editor.apply();
    }

    // Method to display mileage for the selected vehicle
    @SuppressLint({"SetTextI18n", "Range"})
    private void showVehicle(int vehicleIndex) {
        if (vehicleIndex < 0 || vehicleIndex >= userVehicles.size()) {
            Log.e("showVehicle", "Invalid vehicle index: " + vehicleIndex);
            return;
        }

        UserVehicle selectedVehicle = userVehicles.get(vehicleIndex);
        if (selectedVehicle == null) {
            Log.e("showVehicle", "Selected vehicle is null at index: " + vehicleIndex);
            return;
        }

        // Ensure make, model, and year are assigned correctly
        String make = selectedVehicle.getMake();
        String model = selectedVehicle.getModel();
        int year = selectedVehicle.getYear();
        String nickname = selectedVehicle.getNickname();
        String vehicleId = selectedVehicle.getCarId();  // Get the vehicle's unique ID for notifications

        if (make == null || model == null) {
            Log.e("showVehicle", "Vehicle data is incomplete: Make - " + make + ", Model - " + model + ", Year - " + year);
            return;
        }

        // Set Title (use nickname if available)
        String title = (nickname != null && !nickname.isEmpty())
                ? nickname
                : year + " " + make + " " + model;
        binding.selectedCarTitle.setText(title);

        // Call the method to fetch and update the last updated timestamp
        fetchAndUpdateLastUpdated(vehicleId);

        // Set selected car in VariableAccess
        VariableAccess.getInstance().setActiveVehicle(
                String.valueOf(selectedVehicle.getYear()),
                selectedVehicle.getMake(),
                selectedVehicle.getModel()
        );

        // Load the notification settings for the current vehicle
        notificationsOn = loadNotificationPreference(vehicleId);

        // Update the UI based on the loaded notification state
        if (notificationsOn) {
            notificationToggleButton.setText("Turn Off");
            Drawable greenGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.green_border_gradient);
            notificationBar.setBackground(greenGradient);
            notificationText.setText("Notifications for this vehicle are ON");
        } else {
            notificationToggleButton.setText("Turn On");
            Drawable grayGradient = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.gray_border_gradient);
            notificationBar.setBackground(grayGradient);
            notificationText.setText("Notifications for this vehicle are OFF");
        }

        // Set up the notification toggle button to handle toggling for this specific vehicle
        notificationToggleButton.setOnClickListener(v -> toggleNotifications());

        // Callback after configurations are fetched
        Runnable finalCallback = () -> {
            if (VariableAccess.getInstance().getOilConfig() != null
                    && VariableAccess.getInstance().getOilConfigT() != null
                    && VariableAccess.getInstance().getTireConfig() != null
                    && VariableAccess.getInstance().getTireConfigT() != null
                    && VariableAccess.getInstance().getBrakeInspectionConfig() != null
                    && VariableAccess.getInstance().getBrakeInspectionConfigT() != null
                    && VariableAccess.getInstance().getCabinFilterConfig() != null
                    && VariableAccess.getInstance().getCabinFilterConfigT() != null
                    && VariableAccess.getInstance().getCoolantConfig() != null
                    && VariableAccess.getInstance().getCoolantConfigT() != null
                    && VariableAccess.getInstance().getEngineFilterConfig() != null
                    && VariableAccess.getInstance().getEngineFilterConfigT() != null
                    && VariableAccess.getInstance().getSparkPlugsConfig() != null
                    && VariableAccess.getInstance().getSparkPlugsConfigT() != null
                    && VariableAccess.getInstance().getTransmissionConfig() != null
                    && VariableAccess.getInstance().getTransmissionConfigT() != null
            ) {

                // Load full mileage string
                String milesStr = UpcomingMaintenanceMethods.getInstance().concatenateConfigStr(
                        VariableAccess.getInstance().getOilConfig(),
                        VariableAccess.getInstance().getTireConfig(),
                        VariableAccess.getInstance().getBrakeInspectionConfig(),
                        VariableAccess.getInstance().getCabinFilterConfig(),
                        VariableAccess.getInstance().getCoolantConfig(),
                        VariableAccess.getInstance().getEngineFilterConfig(),
                        VariableAccess.getInstance().getSparkPlugsConfig(),
                        VariableAccess.getInstance().getTransmissionConfig(),
                        false
                );

                // Load full time string
                String timeStr = UpcomingMaintenanceMethods.getInstance().concatenateConfigStr(
                        VariableAccess.getInstance().getOilConfigT(),
                        VariableAccess.getInstance().getTireConfigT(),
                        VariableAccess.getInstance().getBrakeInspectionConfigT(),
                        VariableAccess.getInstance().getCabinFilterConfigT(),
                        VariableAccess.getInstance().getCoolantConfigT(),
                        VariableAccess.getInstance().getEngineFilterConfigT(),
                        VariableAccess.getInstance().getSparkPlugsConfigT(),
                        VariableAccess.getInstance().getTransmissionConfigT(),
                        true
                );

                // Preload strings into variables
                VariableAccess.getInstance().setUpcomingMaintenanceMiles(milesStr);
                VariableAccess.getInstance().setUpcomingMaintenanceTime(timeStr);

                // Discover next maintenance
                String nextTitle = VariableAccess.getInstance().getUpcomingMaintenanceMiles();
                String nextMiles = nextTitle;
                String nextTime = VariableAccess.getInstance().getUpcomingMaintenanceTime();

                // Regular expressions to extract mileage and maintenance task
                Pattern mileagePattern = Pattern.compile("(\\d+) miles:");
                Pattern timePattern = Pattern.compile("In\\s+(\\d+)\\s+(days|months|years):");
                Pattern taskPattern = Pattern.compile(":\\s*(.+?)(?=\\n|$)");

                Matcher mileageMatcher = mileagePattern.matcher(nextMiles);
                Matcher timeMatcher = timePattern.matcher(nextTime);
                Matcher taskMatcher = taskPattern.matcher(nextTitle);

                String mileage = null;
                String time = null;
                String maintenanceTask = null;
                int totalDays = 0;

                // Find the first mileage
                if (mileageMatcher.find()) {
                    mileage = mileageMatcher.group(1); // Extract the mileage value
                    nextMiles = mileage + " miles";
                }

                // Find the first time
                if (timeMatcher.find()) {
                    int amount = Integer.parseInt(timeMatcher.group(1)); // Extract the numeric value
                    String unit = timeMatcher.group(2); // Extract the unit (days, months, years)

                    // Convert to days based on the unit
                    switch (unit) {
                        case "days":
                            totalDays = amount;
                            break;
                        case "months":
                            totalDays = amount * 30;
                            break;
                        case "years":
                            totalDays = amount * 360;
                            break;
                    }

                    time = totalDays + "";

                    // Convert it into actual time
                    LocalDate futureDate = LocalDate.now().plusDays(totalDays);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                    nextTime = futureDate.format(formatter);
                }

                // Find the first maintenance task
                if (taskMatcher.find()) {
                    maintenanceTask = taskMatcher.group(1).trim(); // Extract the maintenance task and trim any whitespace
                    nextTitle = maintenanceTask;
                }

                // Assign text to TextViews for maintenance
                nextMaintenanceTitle.setText(nextTitle);
                nextMaintenanceMiles.setText(nextMiles);
                nextMaintenanceDate.setText(nextTime);
            } else {
                Log.d("ERROR", "NULL CONFIGS in showVehicle() method for upcoming maintenance");
            }
        };

        // Fetch configurations and then call final callback
        compileOilConfig(
                () -> compileTireConfig(
                        () -> compileBrakeInspectionConfig(
                                () -> compileCabinFilterConfig(
                                        () -> compileCoolantConfig(
                                                () -> compileEngineFilterConfig(
                                                        () -> compileSparkPlugsConfig(
                                                                () -> compileTransmissionConfig(
                                                                        finalCallback
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        // Fetch additional vehicle details (including mileage)
        userVehicleApi.getVehicleByIndex(vehicleId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Failed to retrieve vehicle details", Toast.LENGTH_SHORT).show();
                    mileageText.setText("Mileage not available");
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    UserVehicle vehicleDetails = parseVehicleDetails(responseBody);

                    if (vehicleDetails == null) {
                        Toast.makeText(getContext(), "Error parsing vehicle details", Toast.LENGTH_SHORT).show();
                        mileageText.setText("Mileage not available");
                        return;
                    }

                    // Update vehicle mileage
                    int mileage = vehicleDetails.getMileage();
                    mileageText.setText(mileage > 0 ? mileage + " miles" : "Mileage not available");

                    // Update last updated timestamp
                    updateLastUpdatedText(vehicleDetails.getLastUpdatedTimestamp());

                    // Set animation for the make
                    String carMake = make.toLowerCase();  // make should be non-null here
                    int animationResource = getAnimationForMake(carMake);

                    Log.d("AnimationCheck", "Setting animation for make: " + carMake + " with resource ID: " + animationResource);

                    // Reset and set the correct animation resource for carAnimationView
                    carAnimationView.cancelAnimation();
                    carAnimationView.clearAnimation();
                    carAnimationView.setAnimation(animationResource);

                    // Handle visibility: if no specific animation is found, display a static car image
                    carImageView.setVisibility(animationResource == 0 ? View.VISIBLE : View.GONE);
                    carAnimationView.setVisibility(animationResource != 0 ? View.VISIBLE : View.GONE);

                    // Play the animation if an animation resource is available
                    if (animationResource != 0) {
                        carAnimationView.playAnimation();
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error reading vehicle details response", Toast.LENGTH_SHORT).show();
                    mileageText.setText("Mileage not available");
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error loading vehicle details", Toast.LENGTH_SHORT).show();
                mileageText.setText("Mileage not available");
            }
        });
    }


    //Method to switch between vehicles
    @SuppressLint("Range")
    private void switchVehicle(int vehicleIndex) {
        if (vehicleIndex >= 1 && vehicleIndex < userVehicles.size()) {
            String userId = getUserIdFromSession();  // Retrieve the user ID
            String primaryVehicleId = getCarId(0);   // The currently active vehicle (index 0)
            String targetVehicleId = getCarId(vehicleIndex);  // Get the target vehicle's ID to swap with
            Log.d("DEBUG",primaryVehicleId);
            Log.d("DEBUG",targetVehicleId);

            // Use Retrofit to make the POST request to swap vehicles
            userVehicleApi.swapVehicles(userId, primaryVehicleId, targetVehicleId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Update UI to reflect the newly active vehicle
                        loadVehiclesFromServer();
                        updateActiveVehicleUI();
                        updateVehicleButtons();
                        Toast.makeText(getContext(), "Vehicle switched successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to swap vehicles", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), "Error swapping vehicles", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressLint({"SetTextI18n", "Range"})
    private void updateActiveVehicleUI() {
        vehicleList.clear();  // Clear the list to update it with the active vehicle

        userVehicleApi.getActiveVehicle(getUserIdFromSession()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        UserVehicle activeVehicle = parseVehicleDetails(response.body().string());

                        if (activeVehicle != null) {
                            // Update the nickname and vehicle details
                            String nickname = activeVehicle.getNickname();
                            String make = activeVehicle.getMake();
                            String model = activeVehicle.getModel();
                            int year = activeVehicle.getYear();
                            String title = (nickname != null && !nickname.isEmpty())
                                    ? nickname
                                    : year + " " + make + " " + model;

                            titleText.setText(title);  // Update title with nickname or default to make/model/year
                            // Any additional UI updates related to the active vehicle
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve active vehicle details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error loading active vehicle", Toast.LENGTH_SHORT).show();
            }
        });

        // Update vehicle buttons as well if needed
        updateVehicleButtons();
    }

    // Prompt user to add a new vehicle
    private void promptAddVehicle() {
        if (!hasNavigatedToAddVehicle) {
            hasNavigatedToAddVehicle = true;

            // Use a Handler to introduce a 300ms delay before navigation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_FirstFragment_to_AddVehicleActivity);
            }, 300); // delay in milliseconds
        }
    }

    // Method to update the button text dynamically based on the number of vehicles
    @SuppressLint({"Range", "SetTextI18n"})
    private void updateVehicleButtons() {
        // Clear existing lists to prevent duplication
        vehicleList.clear();
        vehicleMakes.clear();

        String userId = getUserIdFromSession();

        userVehicleApi.getAllVehicles(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        List<UserVehicle> allVehicles = parseAllVehicles(jsonResponse);

                        userVehicles.clear();
                        userVehicles.addAll(allVehicles);

                        // Populate UI based on the number of vehicles for indices 1, 2, and 3 only
                        for (int i = 1; i <= 3; i++) {
                            if (i < userVehicles.size()) {
                                UserVehicle vehicle = userVehicles.get(i);
                                String vehicleText = (vehicle.getNickname() != null && !vehicle.getNickname().isEmpty())
                                        ? vehicle.getNickname()
                                        : vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel();

                                // Track vehicle information with standardized make
                                String carMake = vehicle.getMake() != null ? vehicle.getMake().toLowerCase() : "";
                                vehicleList.add(vehicleText);
                                vehicleMileage.add(vehicle.getMileage());
                                vehicleMakes.add(carMake);

                                // Log each vehicle’s make and animation resource for debugging
                                int animationResource = getAnimationForMake(carMake);
                                Log.d("AnimationCheck", "Setting vehicle at index " + i + " with make: " + carMake +
                                        " and animation resource: " + animationResource);

                                // Assign each vehicle to a button using the helper method
                                assignVehicleButton(i, vehicleText, vehicle.getMileage(), animationResource);
                            } else {
                                // Show "Add Vehicle" option for empty slots
                                showAddVehicleOption(i);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing vehicle data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve vehicles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error loading vehicles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to assign vehicle data to a specific slot
    @SuppressLint("SetTextI18n")
    private void assignVehicleButton(int index, String vehicleText, int mileage, int animationResource) {
        switch (index) {
            case 1:
                carDetails1.setText(vehicleText);
                carMileage1.setText(mileage + " miles");
                vehicle1Button.setVisibility(View.VISIBLE);
                vehicle1ImageButton.setVisibility(View.GONE);

                // Set animation for vehicle 1 and play it
                carAnimation1.cancelAnimation();
                carAnimation1.clearAnimation();
                carAnimation1.setAnimation(animationResource);
                carAnimation1.playAnimation();

                vehicle1Button.setOnClickListener(v -> switchVehicle(index));
                break;

            case 2:
                carDetails2.setText(vehicleText);
                carMileage2.setText(mileage + " miles");
                vehicle2Button.setVisibility(View.VISIBLE);
                vehicle2ImageButton.setVisibility(View.GONE);

                // Set animation for vehicle 2 and play it
                carAnimation2.cancelAnimation();
                carAnimation2.clearAnimation();
                carAnimation2.setAnimation(animationResource);
                carAnimation2.playAnimation();

                vehicle2Button.setOnClickListener(v -> switchVehicle(index));
                break;

            case 3:
                carDetails3.setText(vehicleText);
                carMileage3.setText(mileage + " miles");
                vehicle3Button.setVisibility(View.VISIBLE);
                vehicle3ImageButton.setVisibility(View.GONE);

                // Set animation for vehicle 3 and play it
                carAnimation3.cancelAnimation();
                carAnimation3.clearAnimation();
                carAnimation3.setAnimation(animationResource);
                carAnimation3.playAnimation();

                vehicle3Button.setOnClickListener(v -> switchVehicle(index));
                break;

            default:
                // Do nothing for case 0 as it's the active vehicle and does not need a button
                break;
        }
    }

    // Helper method to show "Add New Vehicle" button for empty slots
    private void showAddVehicleOption(int index) {
        switch (index) {
            case 1:
                vehicle1ImageButton.setVisibility(View.VISIBLE);
                vehicle1Button.setVisibility(View.GONE);
                vehicle1ImageButton.setOnClickListener(v -> promptAddVehicle());
                break;
            case 2:
                vehicle2ImageButton.setVisibility(View.VISIBLE);
                vehicle2Button.setVisibility(View.GONE);
                vehicle2ImageButton.setOnClickListener(v -> promptAddVehicle());
                break;
            case 3:
                vehicle3ImageButton.setVisibility(View.VISIBLE);
                vehicle3Button.setVisibility(View.GONE);
                vehicle3ImageButton.setOnClickListener(v -> promptAddVehicle());
                break;
        }
    }

    // Helper method to assign vehicle or "Add New Vehicle" functionality to a button
    @SuppressLint("SetTextI18n")
    private void assignVehicleButton(ImageButton addButton, FrameLayout vehicleButton, TextView carDetails,
                                     TextView carMileage, LottieAnimationView carAnimation, int index) {
        if (vehicleList.size() > index && vehicleMileage.size() > index) {
            addButton.setVisibility(View.GONE);
            vehicleButton.setVisibility(View.VISIBLE);
            carDetails.setText(vehicleList.get(index));
            carMileage.setText(vehicleMileage.get(index) + " miles");

            // Set up animation for the vehicle make
            carAnimation.setVisibility(View.VISIBLE);
            carAnimation.setAnimation(getAnimationForMake(vehicleMakes.get(index)));
            carAnimation.playAnimation();

            // Set the button to switch to this vehicle
            vehicleButton.setOnClickListener(v -> switchVehicle(index));
        } else {
            // Display "Add New Vehicle" option if no vehicle exists at this index
            addButton.setVisibility(View.VISIBLE);
            vehicleButton.setVisibility(View.GONE);
            addButton.setOnClickListener(v -> promptAddVehicle());
        }
    }

    // Method to display the alert dialog when no vehicle is selected
    private void showNoVehicleSelectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Vehicle Selected")
                .setMessage("You need to add a vehicle first before using this feature.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    // Navigate to Add Vehicle Activity
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_FirstFragment_to_AddVehicleActivity);
                });
        builder.show();
    }

    private int getAnimationForMake(String carMake) {
        if (carMake == null) {
            Log.d("AnimationCheck", "Car make is null, returning default animation");
            return R.raw.car_animation; // Fallback animation if make is null
        }

        Log.d("AnimationCheck", "Received car make: " + carMake);

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
                Log.d("AnimationCheck", "Unknown car make, returning default animation");
                return R.raw.car_animation;  // Fallback animation for unlisted makes
        }
    }

    // Method to create a dialog box with options for managing a vehicle
    private boolean showVehicleOptionsDialog(int vehicleIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select an action");

        // Options: Update Name, Update Picture, Transfer Vehicle, Delete Vehicle
        String[] options = {"Update Name", "Update Picture", "Transfer Vehicle", "Delete Vehicle"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Update Name
                    showUpdateNameDialog(vehicleIndex);
                    break;
                case 1: // Update Picture
                    handleUpdatePicture();
                    break;
                case 2: // Transfer Vehicle
                    handleTransferVehicle();
                    break;
                case 3: // Delete Vehicle
                    handleDeleteVehicle(vehicleIndex);
                    break;
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();

        return true;
    }

    // Placeholder for handling the update picture action
    private void handleUpdatePicture() {
        Toast.makeText(getContext(), "Update Picture feature coming soon", Toast.LENGTH_SHORT).show();
    }

    // Placeholder for handling the transfer vehicle action
    private void handleTransferVehicle() {
        Toast.makeText(getContext(), "Transfer Vehicle feature coming soon", Toast.LENGTH_SHORT).show();
    }

    // Placeholder for handling the delete vehicle action
    private void handleDeleteVehicle(int vehicleIndex) {
        // Get the car ID based on the vehicle index
        String carId = getCarId(vehicleIndex);

        if (carId == null || carId.isEmpty()) {
            Log.e("DeleteVehicle", "Error: Vehicle ID is missing for index: " + vehicleIndex);
            Toast.makeText(getContext(), "Error: Vehicle ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("DeleteVehicle", "Deleting vehicle with ID: " + carId);

        // Call the Retrofit API to delete the vehicle on the server
        userVehicleApi.deleteActiveVehicle(carId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        Log.d("DeleteVehicle", "Response: " + responseString);  // Log the raw response

                        JSONObject jsonResponse = new JSONObject(responseString);

                        if (jsonResponse.getString("status").equals("success")) {
                            Log.d("DeleteVehicle", "Vehicle deleted successfully");
                            Toast.makeText(getContext(), "Vehicle deleted", Toast.LENGTH_SHORT).show();

                            // Remove the vehicle from the local list
                            userVehicles.remove(vehicleIndex);

                            // If vehicles remain, show the first one or a new one in the list
                            if (!userVehicles.isEmpty()) {
                                currentVehicleIndex = 0;  // Reset to the first vehicle
                                showVehicle(currentVehicleIndex);  // Display the first vehicle after deletion
                            } else {
                                // If no vehicles are left, prompt the user to add a new vehicle
                                promptAddVehicle();
                            }

                            updateVehicleButtons();  // Refresh the vehicle buttons
                        } else {
                            Log.e("DeleteVehicle", "Failed to delete vehicle: " + jsonResponse.getString("message"));
                            Toast.makeText(getContext(), "Failed to delete vehicle: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("DeleteVehicle", "Error parsing response: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("DeleteVehicle", "Error: " + response.message());
                    Toast.makeText(getContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e("DeleteVehicle", "Failed to delete vehicle: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to delete vehicle: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to pop up dialog box to update vehicle name
    @SuppressLint("Range")
    private void showUpdateNameDialog(int vehicleIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Name");

        // Add input field for the new name
        final EditText input = new EditText(getContext());
        input.setHint("Enter new name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Add update button to submit
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString();

            // Handle the case where the name is empty (delete nickname)
            if (newName.isEmpty()) {
                updateVehicleNameOnServer(vehicleIndex, null);  // Passing null to remove the nickname
            } else {
                updateVehicleNameOnServer(vehicleIndex, newName);  // Update with new name
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Helper method to update vehicle name on the server
    private void updateVehicleNameOnServer(int vehicleIndex, String newName) {
        // Get the car ID based on the vehicle index
        String carId = getCarId(vehicleIndex);

        if (carId == null || carId.isEmpty()) {
            Log.e("UpdateVehicle", "Error: Vehicle ID is missing for index: " + vehicleIndex);
            Toast.makeText(getContext(), "Error: Vehicle ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("UpdateVehicle", "Updating vehicle with ID: " + carId + " with new name: " + newName);

        if (newName == null) {
            newName = "";  // Set to an empty string to remove the nickname
        }

        // Call the Retrofit API to update the vehicle nickname on the server
        userVehicleApi.updateVehicleNickname(carId, newName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        Log.d("UpdateVehicle", "Response: " + responseString);  // Log the raw response

                        JSONObject jsonResponse = new JSONObject(responseString);

                        if (jsonResponse.getString("status").equals("success")) {
                            Log.d("UpdateVehicle", "Vehicle name updated successfully");
                            Toast.makeText(getContext(), "Vehicle name updated", Toast.LENGTH_SHORT).show();
                            String newNickname = jsonResponse.getString("message"); // Assuming the new nickname is returned in the message
                            TextView selectedCarTitle = getView().findViewById(R.id.selected_car_title);
                            selectedCarTitle.setText(newNickname);
                            updateActiveVehicleUI();  // Refresh the UI after a successful update
                        } else {
                            Log.e("UpdateVehicle", "Failed to update vehicle name: " + jsonResponse.getString("message"));
                            Toast.makeText(getContext(), "Failed to update vehicle name: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("UpdateVehicle", "Error parsing response: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("UpdateVehicle", "Error: " + response.message());
                    Toast.makeText(getContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e("UpdateVehicle", "Failed to update vehicle name: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to update vehicle name: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Updated getCarId method to retrieve the car ID for a given index
    private String getCarId(int vehicleIndex) {
        if (vehicleIndex >= 0 && vehicleIndex < userVehicles.size()) {
            return userVehicles.get(vehicleIndex).getCarId(); // Return carId as String
        }
        return ""; // Return empty string if index is out of bounds
    }

    private Date parseLastUpdatedTime(String response) {
        try {
            // Log the raw response string for debugging
            Log.d("parseLastUpdatedTime", "Received response: " + response);

            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(response);

            // Check if the "last_updated" field exists and log it
            if (jsonObject.has("last_updated")) {
                String lastUpdatedStr = jsonObject.getString("last_updated");
                Log.d("parseLastUpdatedTime", "Last updated string: " + lastUpdatedStr);

                // Parse the date string using the correct format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                // Log the date format being used for parsing
                Log.d("parseLastUpdatedTime", "Using date format: " + sdf.toPattern());

                // Parse and return the Date object
                Date parsedDate = sdf.parse(lastUpdatedStr);

                // Log the successfully parsed Date object in the desired format
                Log.d("parseLastUpdatedTime", "Parsed date (formatted): " + sdf.format(parsedDate));

                return parsedDate;
            } else {
                // Log if the "last_updated" field is missing
                Log.e("parseLastUpdatedTime", "Error: 'last_updated' field is missing in the JSON response.");
            }
        } catch (Exception e) {
            // Log any exceptions that occur during parsing
            Log.e("parseLastUpdatedTime", "Error parsing date: " + e.getMessage());
            e.printStackTrace();
        }

        // Return null if parsing fails or if the field is missing
        return null;
    }

    private UserVehicle parseVehicleDetails(String jsonResponse) {
        Gson gson = new Gson();
        try {
            // Log the raw JSON response for debugging purposes
            Log.d("parseVehicleDetails", "Received JSON: " + jsonResponse);

            // Parse the top-level JSON object
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Check if the "car" object exists in the JSON response
            if (jsonObject.has("car")) {
                JSONObject carObject = jsonObject.getJSONObject("car");

                // Extract the "last_updated" field
                String lastUpdatedStr = carObject.has("last_updated") ? carObject.getString("last_updated") : null;

                // Log the extracted last updated timestamp for debugging
                Log.d("parseVehicleDetails", "Last updated timestamp: " + lastUpdatedStr);

                // Parse the rest of the car details using Gson
                UserVehicle vehicle = gson.fromJson(carObject.toString(), UserVehicle.class);

                // Manually set the last updated field if it was found
                if (lastUpdatedStr != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date lastUpdatedDate = sdf.parse(lastUpdatedStr);
                    vehicle.setLastUpdatedTimestamp(lastUpdatedDate); // Assuming you have this setter in UserVehicle
                }

                // Log the parsed vehicle object for debugging
                Log.d("parseVehicleDetails", "Parsed vehicle object: " + vehicle.toString());

                return vehicle;
            } else {
                // Log an error if the "car" object is missing in the response
                Log.e("parseVehicleDetails", "Car object is missing in the response");
                return null;
            }
        } catch (JSONException | ParseException e) {
            // Log parsing errors and return null
            Log.e("parseVehicleDetails", "JSON parsing error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private List<UserVehicle> parseAllVehicles(String jsonResponse) {
        Gson gson = new Gson();
        try {
            // Check if response starts with '{' indicating JSON object or '[' for JSON array
            if (!jsonResponse.trim().startsWith("{")) {
                Log.e("parseAllVehicles", "Unexpected response format: " + jsonResponse);
                return new ArrayList<>();
            }

            VehicleResponse vehicleResponse = gson.fromJson(jsonResponse, VehicleResponse.class);
            return vehicleResponse != null ? vehicleResponse.getVehicles() : new ArrayList<>();
        } catch (JsonSyntaxException e) {
            Log.e("parseAllVehicles", "JSON parsing error", e);
            return new ArrayList<>(); // Return empty list on error
        }
    }


    private void compileOilConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkOilConfig = fortuneApi.checkOilConfig(year, make, model);
        checkOilConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setOilConfig(printString1);
                                        VariableAccess.getInstance().setOilConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    private void compileTireConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkTireConfig = fortuneApi.checkTireConfig(year, make, model);
        checkTireConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setTireConfig(printString1);
                                        VariableAccess.getInstance().setTireConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    private void compileBrakeInspectionConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkBrakeInspectionConfig = fortuneApi.checkBrakeInspectionConfig(year, make, model);
        checkBrakeInspectionConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setBrakeInspectionConfig(printString1);
                                        VariableAccess.getInstance().setBrakeInspectionConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    private void compileCabinFilterConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkCabinFilterConfig = fortuneApi.checkCabinFilterConfig(year, make, model);
        checkCabinFilterConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setCabinFilterConfig(printString1);
                                        VariableAccess.getInstance().setCabinFilterConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    private void compileCoolantConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkCoolantConfig = fortuneApi.checkCoolantConfig(year, make, model);
        checkCoolantConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setCoolantConfig(printString1);
                                        VariableAccess.getInstance().setCoolantConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    private void compileEngineFilterConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkEngineFilterConfig = fortuneApi.checkEngineFilterConfig(year, make, model);
        checkEngineFilterConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setEngineFilterConfig(printString1);
                                        VariableAccess.getInstance().setEngineFilterConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    private void compileSparkPlugsConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkSparkPlugsConfig = fortuneApi.checkSparkPlugsConfig(year, make, model);
        checkSparkPlugsConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setSparkPlugsConfig(printString1);
                                        VariableAccess.getInstance().setSparkPlugsConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    private void compileTransmissionConfig(Runnable callback) {
        //Access selected vehicle information
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);

        // API call to the database
        Call<ResponseBody> checkTransmissionConfig = fortuneApi.checkTransmissionConfig(year, make, model);
        checkTransmissionConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Ensure response only contains JSON, handle any extra messages on the server-side
                        if (responseString.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").startsWith("Maintenance details found")) {
                                    //set variables
                                    String type = jsonResponse.getString("maintenance_type");
                                    String miles = jsonResponse.getString("miles_period");
                                    String time = jsonResponse.getString("maintenance_period");

                                    //Set strings
                                    final String printString1 = miles + ":" + type;
                                    final String printString2 = time + ":" + type;

                                    requireActivity().runOnUiThread(() ->
                                    {
                                        VariableAccess.getInstance().setTransmissionConfig(printString1);
                                        VariableAccess.getInstance().setTransmissionConfigT(printString2);
                                    });

                                    if (callback != null) {
                                        callback.run();
                                    }
                                }
                            } else {
                                // Show error message in a toast
                                requireActivity().runOnUiThread(() ->
                                        {
                                            Toast.makeText(requireContext(), "JSON status: failure", Toast.LENGTH_SHORT).show();
                                        }
                                );
                            }
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    {
                                        Toast.makeText(requireContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error 1";

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    // Handle HTTP error response
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Error processing error response.";
                    }
                    final String finalErrorBody = errorBody != null ? errorBody : "Unknown error 2";
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), finalErrorBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Unknown error 3";

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()

                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding to avoid memory leaks
        binding = null;
    }
}