package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UpcomingMaintenanceActivity extends AppCompatActivity {
    private TextView mainText;
    private TextView milesTab;
    private TextView timeTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_maintenance);

        //set up tabs and text as entities
        milesTab = findViewById(R.id.textView_tab_maintenance_by_miles);
        timeTab = findViewById(R.id.textView_tab_maintenance_by_time);
        mainText = findViewById(R.id.textView_upcoming_maintenance_main);

        //Add OnClickListener for each tab
        milesTab.setOnClickListener(v -> loadMiles());
        timeTab.setOnClickListener(v -> loadTime());
    }

    //Maintenance by Miles method
    private void loadMiles() {
        //change backgrounds
        milesTab.setBackground(getDrawable(R.drawable.tab_background_selected));
        timeTab.setBackground(getDrawable(R.drawable.tab_background_unselected));

        //change main text
        mainText.setText("This is the Maintenance by MILES text");
    }

    //Maintenance by Time method
    private void loadTime() {
        //change backgrounds
        milesTab.setBackground(getDrawable(R.drawable.tab_background_unselected));
        timeTab.setBackground(getDrawable(R.drawable.tab_background_selected));

        //change main text
        mainText.setText("This is the Maintenance by TIME text");
    }
}
