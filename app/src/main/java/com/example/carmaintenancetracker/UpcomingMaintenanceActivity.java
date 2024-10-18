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
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
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
    private String blankString;

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

        // load first page
        loadMiles();
    }

    //Maintenance by Miles method
    private void loadMiles() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));

        //update miles text based on vehicle status

        blankString = getResources().getString(R.string.upcoming_maintenance_miles_text);

        testPrintOilConfig();

        //change main text
        mainText.setText(blankString);
    }

    //Maintenance by Time method
    private void loadTime() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));

        //update time text based on vehicle status
        blankString = getResources().getString(R.string.upcoming_maintenance_time_text);

        //change main text
        mainText.setText(blankString);
    }

    private void testPrintOilConfig() {
        // API call to the database
        Call<ResponseBody> checkOilConfig2 = api.checkOilConfig(year, make, model);
        checkOilConfig2.enqueue(new Callback<ResponseBody>() {
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
                                    String printString = "At ";
                                    printString += jsonResponse.getString("miles_period");
                                    printString += " miles: \n";
                                    printString += jsonResponse.getString("maintenance_type");
                                    printString += "\n";

                                    // Ensure UI updates are done on the main thread
                                    String finalPrintString = printString;
                                    if (isAdded()) {
                                        requireActivity().runOnUiThread(() -> {
                                            blankString = finalPrintString;
                                            mainText.setText(blankString);
                                        });
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

}

