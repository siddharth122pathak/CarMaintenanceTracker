package com.example.carmaintenancetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class addnewmaint extends AppCompatActivity {

    private EditText editTextDate;
    private Button saveButton;
    private ImageButton calendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_maint_add);

        // Find views by ID
        editTextDate = findViewById(R.id.editTextDate2); // The date input field
        saveButton = findViewById(R.id.button2); // The save button
        calendarButton = findViewById(R.id.imageButton3); // The calendar button

        // Set an OnClickListener to open a date picker dialog
        calendarButton.setOnClickListener(v -> showDatePickerDialog());

        // Set an OnClickListener for the Save button
        saveButton.setOnClickListener(v -> saveMaintenance());
    }

    // Show a DatePickerDialog when click on calendar
    private void showDatePickerDialog() {
        // current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Update the EditText with the selected date
            editTextDate.setText(String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear));
        }, year, month, day);

        datePickerDialog.show();
    }

    // save operation when the Save button is clicked
    private void saveMaintenance() {
        // Get the entered date from the EditText
        String enteredDate = editTextDate.getText().toString();

        // Show a simple message to show something to handling input ( not final spit balling here)
        if (!enteredDate.isEmpty()) {
            Toast.makeText(this, "Maintenance saved for: " + enteredDate, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a maintenance date!", Toast.LENGTH_SHORT).show();
        }

        // I assume database logic here?
    }
}