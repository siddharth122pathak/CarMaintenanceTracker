package com.example.carmaintenancetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.carmaintenancetracker.api.MaintenanceApi;
import com.example.carmaintenancetracker.UserVehicleApi;
import com.example.carmaintenancetracker.RetrofitClient;

import java.util.Calendar;
import java.util.Objects;

public class addnewmaint extends Fragment {

    private EditText editTextDate;
    private Button saveButton;
    private ImageButton calendarButton;
    private Spinner maintenanceActionSpinner;
    private ImageButton addMoreButton;
    private EditText mileageEditText;
    private TextView selectedCarTitle;

    private String carId; // To store the active car ID
    private String userId = "123"; // Replace with actual user ID if needed

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.new_maint_add, container, false);

        // Initialize the Spinner
        maintenanceActionSpinner = view.findViewById(R.id.maintenance_action_spinner);
        editTextDate = view.findViewById(R.id.editTextDate2); // The date input field
        saveButton = view.findViewById(R.id.button2); // The save button
        calendarButton = view.findViewById(R.id.imageButton3); // The calendar button
        addMoreButton = view.findViewById(R.id.add_more_button); // The add more button
        mileageEditText = view.findViewById(R.id.mileage_input); // Mileage input
        selectedCarTitle = view.findViewById(R.id.selected_car_title3); // Car nickname or make/model/year

        // Set an OnClickListener for the calendar button to open the date picker dialog
        calendarButton.setOnClickListener(v -> showDatePickerDialog());

        // Set an OnClickListener for the add more button to open the dialog
        addMoreButton.setOnClickListener(v -> showAddMoreDialog());

        // Set an OnClickListener for the save button
        saveButton.setOnClickListener(v -> saveMaintenance());

        // Fetch and display the active car's information
        getActiveVehicle();

        return view;
    }

    private String getUserIdFromSession() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        Log.d("USER_ID", "User ID: " + userId);
        return sharedPreferences.getString("userId", null);
    }

    private void getActiveVehicle() {
        UserVehicleApi vehicleApi = RetrofitClient.getRetrofitInstance().create(UserVehicleApi.class);
        Call<ResponseBody> call = vehicleApi.getActiveVehicle(getUserIdFromSession());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d("API_RESPONSE", "Response: " + responseString); // Log the full response
                        JSONObject jsonResponse = new JSONObject(responseString);

                        if (jsonResponse.has("car_id")) {  // Check if the response has the car_id field
                            carId = jsonResponse.getString("car_id");
                            String nickname = jsonResponse.optString("nickname", "");
                            String make = jsonResponse.optString("make", "");
                            String model = jsonResponse.optString("model", "");
                            String year = jsonResponse.optString("year", "");

                            // Update the UI with the vehicle info
                            if (!nickname.isEmpty()) {
                                selectedCarTitle.setText(nickname);
                            } else {
                                selectedCarTitle.setText(String.format("%s %s %s", year, make, model));
                            }
                        } else {
                            Toast.makeText(getContext(), "No active vehicle found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to get active vehicle", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show a DatePickerDialog when the calendar button is clicked
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            editTextDate.setText(String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear));
        }, year, month, day);

        datePickerDialog.show();
    }

    // Handle the save operation when the save button is clicked
    private void saveMaintenance() {
        String enteredDate = editTextDate.getText().toString(); // Get the entered date
        String newMileage = mileageEditText.getText().toString(); // Get the entered mileage

        // Ensure we have an active car ID before proceeding
        if (carId == null || carId.isEmpty()) {
            Toast.makeText(getContext(), "No active vehicle selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Case 1: Update mileage if entered
        if (!newMileage.isEmpty()) {
            // Call API to update mileage for the active vehicle
            UserVehicleApi vehicleApi = RetrofitClient.getRetrofitInstance().create(UserVehicleApi.class);
            Call<ResponseBody> call = vehicleApi.updateMileage(carId, Integer.parseInt(newMileage));

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Mileage updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update mileage", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Case 2: Add maintenance if date is entered
        if (!enteredDate.isEmpty()) {
            // Call API to add the maintenance with the selected date
            MaintenanceApi maintenanceApi = RetrofitClient.getRetrofitInstance().create(MaintenanceApi.class);
            Call<ResponseBody> call = maintenanceApi.addMaintenance("Maintenance Type", enteredDate, "Description");

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Maintenance saved successfully", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(addnewmaint.this)
                                .navigate(R.id.action_addnewmaint_to_FirstFragment);
                    } else {
                        Toast.makeText(getContext(), "Failed to save maintenance", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Case 3: If neither mileage nor date is entered, show a warning
        if (newMileage.isEmpty() && enteredDate.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a mileage or a date for maintenance!", Toast.LENGTH_SHORT).show();
        }
    }

    // Show the "Add More" dialog
    private void showAddMoreDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_maintenance, null);

        // Find views inside the dialog
        EditText maintenancePerformed = dialogView.findViewById(R.id.editTextMaintenancePerformed);
        EditText datePerformed = dialogView.findViewById(R.id.editTextDatePerformed);

        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setTitle("Add Maintenance")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Handle saving the entered data
                    String maintenance = maintenancePerformed.getText().toString();
                    String date = datePerformed.getText().toString();

                    if (!maintenance.isEmpty() && !date.isEmpty()) {
                        // Call API to add additional maintenance
                        MaintenanceApi maintenanceApi = RetrofitClient.getRetrofitInstance().create(MaintenanceApi.class);
                        Call<ResponseBody> call = maintenanceApi.addAdditionalMaintenance(maintenance, date);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Additional maintenance added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to add maintenance", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Please enter both maintenance and date", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}