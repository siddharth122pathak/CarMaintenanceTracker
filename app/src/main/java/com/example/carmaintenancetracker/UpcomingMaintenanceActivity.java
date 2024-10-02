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

public class UpcomingMaintenanceActivity extends Fragment {
    private TextView mainText;
    private TextView milesTab;
    private TextView timeTab;

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

        //Add OnClickListener for each tab
        milesTab.setOnClickListener(v -> loadMiles());
        timeTab.setOnClickListener(v -> loadTime());
    }

    //Maintenance by Miles method
    private void loadMiles() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));

        //update miles text based on vehicle status


        //change main text
        mainText.setText(R.string.upcoming_maintenance_miles_text);
    }

    //Maintenance by Time method
    private void loadTime() {
        //change backgrounds
        milesTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_unselected));
        timeTab.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.tab_background_selected));

        //update time text based on vehicle status


        //change main text
        mainText.setText(R.string.upcoming_maintenance_time_text);
    }
}
