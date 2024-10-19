package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.util.Log;
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
        String year = VariableAccess.getInstance().getActiveVehicle().get(0);
        String make = VariableAccess.getInstance().getActiveVehicle().get(1);
        String model = VariableAccess.getInstance().getActiveVehicle().get(2);
        String ymmFinal = year + " " + make + " " + model;
        selectedCar.setText(ymmFinal);

        // load first page
        loadMiles();
    }

    //Maintenance by Miles method
    private void loadMiles() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));

        //change main text to default
        String mainStr = getResources().getString(R.string.upcoming_maintenance_miles_text);

        mainStr = UpcomingMaintenanceMethods.getInstance().concatenateConfigStr(
                VariableAccess.getInstance().getOilConfig(),
                "",
                "",
                "",
                ""
        );

        //change main text
        mainText.setText(mainStr);
    }

    //Maintenance by Time method
    private void loadTime() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));

        //update time text based on vehicle status
        //oilConfigString = getResources().getString(R.string.upcoming_maintenance_time_text);

        //change main text
        mainText.setText(VariableAccess.getInstance().getUpcomingMaintenanceTime());
    }

}

