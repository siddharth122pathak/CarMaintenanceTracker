package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.example.carmaintenancetracker.databinding.ActivityUpcomingMaintenanceBinding;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        testPrintOilConfig();

        //check that the vehicle exists in the database
        /*if (vehicleExists()) {

            //get an array of maintenance tasks organized by mileage
            Task tasks[] = organizeTasksByMiles(vehicle);

            //print the array to the miles string
            R.string.upcoming_maintenance_miles_text = printTasksByMiles(tasks);
        } else {
            // Handle error
        }*/

        //change main text
        //mainText.setText(R.string.upcoming_maintenance_miles_text);
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

    //method to check if the vehicle exists in the db
    private void testPrintOilConfig() {
        //api call to the db
        Call<ResponseBody> checkOilConfig = api.checkOilConfig(year, make, model);
        checkOilConfig.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        // Remove any unnecessary connection messages
                        if (responseString.startsWith("Connected successfully to the database!")) {
                            responseString = responseString.replace("Connected successfully to the database!", "").trim();
                        }

                        if (responseString.trim().startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);
                            if (jsonResponse.getString("status").equals("success")) {
                                if (jsonResponse.getString("message").equals("Maintenance detail found")) {
                                    String printString = "At ";
                                    printString += jsonResponse.getString("miles_period");
                                    printString += " miles: \n";
                                    printString += jsonResponse.getString("maintenance_type");
                                    printString += "\n";
                                    mainText.setText(printString);
                                }
                            } else {
                                // show error
                                Toast.makeText(requireContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), response.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
