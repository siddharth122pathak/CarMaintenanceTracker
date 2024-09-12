package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.airbnb.lottie.LottieAnimationView;
import androidx.appcompat.app.AppCompatActivity;

public class AddVehicleActivity extends AppCompatActivity {

    private LottieAnimationView carAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        carAnimationView = findViewById(R.id.carAnimation);

        // Set up Car Make Spinner with custom layout
        Spinner spinnerCarMake = findViewById(R.id.spinnerCarMake);
        ArrayAdapter<CharSequence> adapterCarMake = ArrayAdapter.createFromResource(this,
                R.array.car_makes, R.layout.spinner_item);
        adapterCarMake.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarMake.setAdapter(adapterCarMake);

        // Set up Car Model Spinner with custom layout
        Spinner spinnerCarModel = findViewById(R.id.spinnerCarModel);
        ArrayAdapter<CharSequence> adapterCarModel = ArrayAdapter.createFromResource(this,
                R.array.car_models, R.layout.spinner_item);
        adapterCarModel.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarModel.setAdapter(adapterCarModel);

        // Set up Year Spinner with custom layout
        Spinner spinnerCarYear = findViewById(R.id.spinnerCarYear);
        ArrayAdapter<CharSequence> adapterCarYear = ArrayAdapter.createFromResource(this,
                R.array.car_years, R.layout.spinner_item);
        adapterCarYear.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarYear.setAdapter(adapterCarYear);

        // Listener for Car Make Spinner to change the animation dynamically
        spinnerCarMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCarMake = parent.getItemAtPosition(position).toString();
                updateCarAnimation(selectedCarMake);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void updateCarAnimation(String carMake) {
        // Change the animation based on selected car make
        switch (carMake) {
            case "Toyota":
                carAnimationView.setAnimation(R.raw.car_animation_toyota);
                break;
            case "Honda":
                carAnimationView.setAnimation(R.raw.car_animation_honda);
                break;
            case "Ford":
                carAnimationView.setAnimation(R.raw.car_animation_ford);
                break;
            case "Chevrolet":
                carAnimationView.setAnimation(R.raw.car_animation_chevrolet);
                break;
            case "BMW":
                carAnimationView.setAnimation(R.raw.car_animation_bmw);
                break;
            default:
                carAnimationView.setAnimation(R.raw.car_animation); // Default animation
                break;
        }
        carAnimationView.playAnimation(); // Start playing the new animation
    }
}