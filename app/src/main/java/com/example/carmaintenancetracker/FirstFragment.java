package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.app.Activity.RESULT_OK;

/** @noinspection CallToPrintStackTrace*/
public class FirstFragment extends Fragment {

    private static final int ADD_VEHICLE_REQUEST_CODE = 1;
    private FragmentFirstBinding binding;
    private TextView mileageText;
    private Button notificationToggleButton;
    private View notificationBar;
    private TextView notificationText;
    private boolean notificationsOn = false;
    public TextView titleText;
    private ImageView lastUpdatedFlareIcon;
    private TextView lastUpdatedText;
    private long lastUpdatedTimestamp;
    private ImageView carImageView;
    private LottieAnimationView carAnimationView;
    private FrameLayout vehicle1Button, vehicle2Button, vehicle3Button;
    private LottieAnimationView carAnimation1, carAnimation2, carAnimation3;
    private TextView carDetails1, carMileage1, carDetails2, carMileage2, carDetails3, carMileage3;
    private ImageButton vehicle1ImageButton, vehicle2ImageButton, vehicle3ImageButton;
    private boolean hasNavigatedToAddVehicle = false;

    private final List<Integer> vehicleMileage = new ArrayList<>();
    private final List<String> vehicleList = new ArrayList<>();
    private final List<String> vehicleMakes = new ArrayList<>();
    private final List<UserVehicle> userVehicles = new ArrayList<>(); // Store UserVehicle objects

    private int currentVehicleIndex = 0;

    private UserVehicleApi userVehicleApi;  // Retrofit API interface
    UserVehicle userVehicle = new UserVehicle();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        // Initialize Retrofit and API
        userVehicleApi = RetrofitClient.getRetrofitInstance().create(UserVehicleApi.class);

        loadVehiclesFromServer();
        return binding.getRoot();
    }

    private void setActiveVehicle(String carId) {
        userVehicleApi.setActiveVehicle(carId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Active vehicle updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update active vehicle.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error setting active vehicle.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMileage(String carId, int newMileage) {
        userVehicleApi.updateMileage(carId, newMileage).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Mileage updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update mileage.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error updating mileage.", Toast.LENGTH_SHORT).show();
            }
        });
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
    private String getUserIdFromSession() {
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

        // Fetch vehicles from server instead of local database
        loadVehiclesFromServer();

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

        //assign upcoming maintenance strings
        VariableAccess.getInstance().setUpcomingMaintenanceMiles("testing miles");
        VariableAccess.getInstance().setUpcomingMaintenanceTime("testing time");
    }

    // Method to set up vehicle buttons
    private void setupVehicleButtons(View view) {
        ImageButton vehicle1Button = view.findViewById(R.id.btn_vehicle_1);
        ImageButton vehicle2Button = view.findViewById(R.id.btn_vehicle_2);
        ImageButton vehicle3Button = view.findViewById(R.id.btn_vehicle_3);

        // Set up button click listeners to swap or add vehicles
        vehicle1Button.setOnClickListener(v -> switchOrAddVehicle(1));
        vehicle2Button.setOnClickListener(v -> switchOrAddVehicle(2));
        vehicle3Button.setOnClickListener(v -> switchOrAddVehicle(3));
    }

    // Method to handle switching to or adding a vehicle
    private void switchOrAddVehicle(int vehicleIndex) {
        if (vehicleIndex <= vehicleList.size()) {
            // Get car ID for the selected vehicle
            String carId = getCarId(vehicleIndex - 1); // Adjust index for list (0-based)

            // Call API to set this vehicle as active
            userVehicleApi.setActiveVehicle(carId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Vehicle switched", Toast.LENGTH_SHORT).show();
                        showVehicle(vehicleIndex - 1);  // Show the selected vehicle
                    } else {
                        Toast.makeText(getContext(), "Failed to switch vehicle", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), "Error switching vehicle", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If vehicleIndex exceeds list size, it indicates adding a new vehicle
            promptAddVehicle();
        }
    }

    // Method to update the last updated text
    @SuppressLint("SetTextI18n")
    private void updateLastUpdatedText(long lastUpdatedTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy @ HH:mm", Locale.getDefault());

        // If no update has been made, use placeholder text
        if (lastUpdatedTimestamp == 0) {
            lastUpdatedText.setText("Last updated: never");
            lastUpdatedFlareIcon.setVisibility(View.VISIBLE);
            return;
        }

        Date lastUpdated = new Date(lastUpdatedTimestamp);
        lastUpdatedText.setText("Last updated: " + sdf.format(lastUpdated));

        // Check if 3 months have passed since the last update
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastUpdatedTimestamp);
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
                        long updatedTimestamp = parseLastUpdatedTime(responseBody);
                        updateLastUpdatedText(updatedTimestamp); // Refresh the "Last updated" text and icon
                    } catch (IOException e) {
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
        // Check if there are any vehicles added
        if (vehicleList.isEmpty()) {
            showNoVehicleSelectedDialog();
            return;
        }

        notificationsOn = !notificationsOn; // Toggle the notification state

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

        // Update the notification setting on the server
        String carId = getCarId(currentVehicleIndex);
        userVehicleApi.updateNotificationSetting(carId, notificationsOn ? 1 : 0).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Notification setting updated", Toast.LENGTH_SHORT).show();
                } else {
                    // Revert the UI and toggle if update fails
                    notificationsOn = !notificationsOn;
                    revertNotificationUI();
                    Toast.makeText(getContext(), "Failed to update notification setting", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                // Revert the UI and toggle if there's a server error
                notificationsOn = !notificationsOn;
                revertNotificationUI();
                Toast.makeText(getContext(), "Error updating notification setting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to revert the UI to previous notification state if update fails
    @SuppressLint("SetTextI18n")
    private void revertNotificationUI() {
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

        // Check if make and model are correctly assigned
        String make = selectedVehicle.getMake();
        String model = selectedVehicle.getModel();
        String year = selectedVehicle.getYear();
        String nickname = selectedVehicle.getNickname();

        if (make == null || model == null || year == null) {
            Log.e("showVehicle", "Vehicle data is incomplete: Make - " + make + ", Model - " + model + ", Year - " + year);
            return;
        }

        // Set Title
        String title = (nickname != null && !nickname.isEmpty())
                ? nickname
                : year + " " + make + " " + model;
        binding.selectedCarTitle.setText(title);

        // Fetch additional vehicle details if needed (e.g., mileage and last update time)
        userVehicleApi.getVehicleByIndex(selectedVehicle.getCarId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Failed to retrieve vehicle details", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    UserVehicle vehicleDetails = parseVehicleDetails(responseBody);

                    if (vehicleDetails == null) {
                        Toast.makeText(getContext(), "Error parsing vehicle details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Display vehicle mileage
                    int mileage = vehicleDetails.getMileage();
                    mileageText.setText(mileage > 0 ? mileage + " miles" : "Mileage not available");

                    // Notification settings update
                    notificationsOn = vehicleDetails.isNotificationsOn();
                    notificationToggleButton.setText(notificationsOn ? "Turn Off" : "Turn On");
                    notificationText.setText(notificationsOn ? "Notifications for this vehicle are ON" : "Notifications for this vehicle are OFF");

                    // Background Gradient Update
                    Context context = getContext();
                    if (context != null) {
                        Drawable gradient = ContextCompat.getDrawable(context,
                                notificationsOn ? R.drawable.green_border_gradient : R.drawable.gray_border_gradient);
                        notificationBar.setBackground(gradient);
                    }

                    // Last updated timestamp
                    updateLastUpdatedText(vehicleDetails.getLastUpdatedTimestamp());

                    // Set Animation for the make
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
                    Log.e("showVehicle", "IOException during response parsing", e);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error loading vehicle details", Toast.LENGTH_SHORT).show();
                Log.e("showVehicle", "API call failed", t);
            }
        });
    }



    // Switch between vehicles based on index
    @SuppressLint("Range")
    private void switchVehicle(int vehicleIndex) {
        if (vehicleIndex >= 1 && vehicleIndex < vehicleList.size()) {
            // Swap the data between the active vehicle (index 1) and the selected vehicle (vehicleIndex + 1)
            String primaryVehicleId = getCarId(0);  // Active vehicle is at index 0
            String targetVehicleId = getCarId(vehicleIndex);  // Get the target vehicle's ID

            // API call to swap vehicles on the server
            userVehicleApi.swapVehicles(primaryVehicleId, targetVehicleId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // API call to set the active vehicle on the server
                        userVehicleApi.setActiveVehicle(primaryVehicleId).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    // Update UI to reflect the newly active vehicle
                                    updateActiveVehicleUI();
                                    updateVehicleButtons();
                                    Toast.makeText(getContext(), "Vehicle switched successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to set active vehicle", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                                Toast.makeText(getContext(), "Error setting active vehicle", Toast.LENGTH_SHORT).show();
                            }
                        });
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

    @SuppressLint({"Range", "SetTextI18n"})
    private void updateActiveVehicleUI() {
        vehicleList.clear();  // Clear the list to update it with the active vehicle

        // Fetch the active vehicle's details from the server
        userVehicleApi.getActiveVehicle().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Parse the response to extract active vehicle details
                        UserVehicle activeVehicle = parseVehicleDetails(response.body().string());

                        // Extract and format the vehicle details
                        String make = activeVehicle.getMake();
                        String model = activeVehicle.getModel();
                        String year = activeVehicle.getYear();
                        String license = activeVehicle.getNickname();
                        int mileage = activeVehicle.getMileage();

                        // Add vehicle details to the list
                        vehicleList.add(make + " " + model + " " + year + " " + license);

                        // Update title based on license or year, make, and model
                        if (license != null && !license.isEmpty()) {
                            titleText.setText(license);  // Use nickname if available
                        } else {
                            titleText.setText(year + " " + make + " " + model);
                        }

                        // Update mileage display for the active vehicle
                        vehicleMileage.clear();
                        vehicleMileage.add(mileage);
                        mileageText.setText(mileage > 0 ? mileage + " miles" : "Mileage not available");

                        // Display the active vehicle in the UI
                        showVehicle(0);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing vehicle details", Toast.LENGTH_SHORT).show();
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

        // Update vehicle buttons
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
        vehicleMileage.clear();
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

                vehicle1Button.setOnClickListener(v -> showVehicle(index));
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

                vehicle2Button.setOnClickListener(v -> showVehicle(index));
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

                vehicle3Button.setOnClickListener(v -> showVehicle(index));
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
        Toast.makeText(getContext(), "Delete Vehicle feature coming soon", Toast.LENGTH_SHORT).show();
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

            // Get the vehicle ID based on the vehicle index
            String vehicleId = getCarId(vehicleIndex);

            // Handle the case where the name is empty (delete nickname)
            if (newName.isEmpty()) {
                updateVehicleNameOnServer(vehicleId, null);
            } else {
                updateVehicleNameOnServer(vehicleId, newName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Helper method to update vehicle name on the server
    private void updateVehicleNameOnServer(String vehicleId, String newName) {
        Call<ResponseBody> call;
        if (newName == null) {
            call = userVehicleApi.updateVehicleNickname(vehicleId, ""); // Set nickname to empty string for deletion
        } else {
            call = userVehicleApi.updateVehicleNickname(vehicleId, newName);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Vehicle name updated", Toast.LENGTH_SHORT).show();
                    updateActiveVehicleUI(); // Refresh UI to reflect updated name
                } else {
                    Toast.makeText(getContext(), "Failed to update vehicle name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error updating vehicle name", Toast.LENGTH_SHORT).show();
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

    private long parseLastUpdatedTime(String response) {
        try {
            return Long.parseLong(response.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0; // Return 0 if parsing fails
        }
    }

    private UserVehicle parseVehicleDetails(String jsonResponse) {
        Gson gson = new Gson();
        try {
            // Check if the response is in the expected JSON object format
            if (jsonResponse.startsWith("{") || jsonResponse.startsWith("[")) {
                // Parse the JSON into a UserVehicle object
                return gson.fromJson(jsonResponse, UserVehicle.class);
            } else {
                // Log an error and return null or a default UserVehicle if the response is unexpected
                Log.e("parseVehicleDetails", "Unexpected response format: " + jsonResponse);
                return null;
            }
        } catch (JsonSyntaxException e) {
            // Log the error and handle the parsing exception
            Log.e("parseVehicleDetails", "JSON parsing error", e);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding to avoid memory leaks
        binding = null;
    }
}