package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.example.carmaintenancetracker.databinding.ActivityUpcomingMaintenanceBinding;
import com.example.carmaintenancetracker.FirstFragment;

import java.util.StringTokenizer;

public class UpcomingMaintenanceActivity extends Fragment {
    private TextView mainText;
    private TextView milesTab;
    private TextView timeTab;
    private TextView selectedCar;
    private UserApi api;
    public String year;
    public String make;
    public String model;

    //View binding for the fragment's layout
    private ActivityUpcomingMaintenanceBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout using binding
        binding = ActivityUpcomingMaintenanceBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set up tabs and text as entities
        milesTab = view.findViewById(R.id.textView_tab_maintenance_by_miles);
        timeTab = view.findViewById(R.id.textView_tab_maintenance_by_time);
        mainText = view.findViewById(R.id.textView_upcoming_maintenance_main);
        selectedCar = view.findViewById(R.id.selected_car_title2);

        //Add OnClickListener for each tab
        milesTab.setOnClickListener(v -> loadMiles());
        timeTab.setOnClickListener(v -> loadTime());

        //get selected car
        String yearMakeModel = "2001 Toyota Camry";
        selectedCar.setText(yearMakeModel);

        //parse string to assign year/make/model
        StringTokenizer tokenizer = new StringTokenizer(selectedCar.getText().toString(), " ");
        year = tokenizer.nextToken();
        make = tokenizer.nextToken();
        model = tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {model += " " + tokenizer.nextToken();}

        //set up API client
        api = RetrofitClient.getRetrofitInstance().create(UserApi.class);
    }

    //Maintenance by Miles method
    private void loadMiles() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));

        //update miles text based on vehicle status
        //check that the vehicle exists in the database
        /*if (vehicle.exists()) {

            //get an array of maintenance tasks organized by mileage
            Task arr[] = organizeTasksByMiles(vehicle);

            //print the array to the miles string
            R.string.upcoming_maintenance_miles_text = printTasksByMiles(arr);
        } else {
            // Handle error
        }*/

        //change main text
        mainText.setText(R.string.upcoming_maintenance_miles_text);
    }

    //Maintenance by Time method
    private void loadTime() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));

        //update time text based on vehicle status
        /*
        //call the user
        Call<List<User>> user = apiInterface.getUser();

        //get the user's vehicle
        Vehicle<List<User>> vehicle = user.vehicle();

        //check that the vehicle exists in the database
        if (vehicle.exists()) {

            //get an array of maintenance tasks organized by time
            Task arr[] = organizeTasksByTime(vehicle);

            //print the array to the time string
            R.string.upcoming_maintenance_time_text = printTasksByMiles(arr);
        } else {
            // Handle error
        }
        */

        //change main text
        mainText.setText(R.string.upcoming_maintenance_time_text);
    }
}
