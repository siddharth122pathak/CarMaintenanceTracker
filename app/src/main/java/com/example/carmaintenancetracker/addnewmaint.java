package com.example.carmaintenancetracker;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.carmaintenancetracker.api.MaintenanceApi;
import com.example.carmaintenancetracker.RetrofitClient;

import java.util.Calendar;


public class addnewmaint extends Fragment {

    private EditText editTextDate;
    private Button saveButton;
    private ImageButton calendarButton;
    private Spinner maintenanceActionSpinner;
    private ImageButton addMoreButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.new_maint_add, container, false);

        // Initialize the Spinner
        maintenanceActionSpinner = view.findViewById(R.id.maintenance_action_spinner);

        // Find views by ID within the fragment's view
        editTextDate = view.findViewById(R.id.editTextDate2); // The date input field
        saveButton = view.findViewById(R.id.button2); // The save button
        calendarButton = view.findViewById(R.id.imageButton3); // The calendar button
        addMoreButton = view.findViewById(R.id.add_more_button); // The add more button

        // Create a list of items for the Spinner
        String[] maintenanceActions = new String[] {
                "Oil Change", "Tire Rotation", "Brake Check", "Engine Inspection"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, maintenanceActions);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Spinner
        maintenanceActionSpinner.setAdapter(adapter);

        // Set an OnClickListener for the calendar button to open the date picker dialog
        calendarButton.setOnClickListener(v -> showDatePickerDialog());

        // Set an OnClickListener for the add more button to open the dialog
        addMoreButton.setOnClickListener(v -> showAddMoreDialog());

        // Set an OnClickListener for the save button
        saveButton.setOnClickListener(v -> saveMaintenance());

        return view;
    }

    // Show a DatePickerDialog when the calendar button is clicked
    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            // Update the EditText with the selected date
            editTextDate.setText(String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear));
        }, year, month, day);

        datePickerDialog.show();
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

    // Handle the save operation when the save button is clicked
    private void saveMaintenance() {
        // Get the entered date from the EditText
        String enteredDate = editTextDate.getText().toString();
        String maintenanceType = maintenanceActionSpinner.getSelectedItem().toString();

        if (!enteredDate.isEmpty()) {
            // Call API to add the maintenance
            MaintenanceApi maintenanceApi = RetrofitClient.getRetrofitInstance().create(MaintenanceApi.class);
            Call<ResponseBody> call = maintenanceApi.addMaintenance(maintenanceType, enteredDate, "");

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Maintenance saved successfully", Toast.LENGTH_SHORT).show();
                        // Navigate back after saving
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
        } else {
            Toast.makeText(getContext(), "Please enter a maintenance date!", Toast.LENGTH_SHORT).show();
        }
    }
}