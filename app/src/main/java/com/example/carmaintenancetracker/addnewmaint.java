package com.example.carmaintenancetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class addnewmaint extends Fragment {

    private EditText editTextDate;
    private Button saveButton;
    private ImageButton calendarButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.new_maint_add, container, false);

        // Find views by ID within the fragment's view
        editTextDate = view.findViewById(R.id.editTextDate2); // The date input field
        saveButton = view.findViewById(R.id.button2); // The save button
        calendarButton = view.findViewById(R.id.imageButton3); // The calendar button

        // Set an OnClickListener for the calendar button to open the date picker dialog
        calendarButton.setOnClickListener(v -> showDatePickerDialog());

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

    // Handle the save operation when the save button is clicked
    private void saveMaintenance() {
        // Get the entered date from the EditText
        String enteredDate = editTextDate.getText().toString();

        // Show a simple message (Toast) as an example of handling input
        if (!enteredDate.isEmpty()) {
            Toast.makeText(getContext(), "Maintenance saved for: " + enteredDate, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Please enter a maintenance date!", Toast.LENGTH_SHORT).show();
        }

        // Add your DB logic or further actions here
    }
}