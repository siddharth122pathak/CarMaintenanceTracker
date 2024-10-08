package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.airbnb.lottie.LottieAnimationView;

public class AddVehicleActivity extends Fragment {

    private LottieAnimationView carAnimationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_vehicle, container, false);

        carAnimationView = rootView.findViewById(R.id.carAnimation);

        // Set up Car Make Spinner with custom layout
        Spinner spinnerCarMake = rootView.findViewById(R.id.spinnerCarMake);
        ArrayAdapter<CharSequence> adapterCarMake = ArrayAdapter.createFromResource(getContext(),
                R.array.car_makes, R.layout.spinner_item);
        adapterCarMake.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarMake.setAdapter(adapterCarMake);

        // Set up Car Model Spinner with custom layout
        Spinner spinnerCarModel = rootView.findViewById(R.id.spinnerCarModel);
        ArrayAdapter<CharSequence> adapterCarModel = ArrayAdapter.createFromResource(getContext(),
                R.array.car_models, R.layout.spinner_item);
        adapterCarModel.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarModel.setAdapter(adapterCarModel);

        // Set up Year Spinner with custom layout
        Spinner spinnerCarYear = rootView.findViewById(R.id.spinnerCarYear);
        ArrayAdapter<CharSequence> adapterCarYear = ArrayAdapter.createFromResource(getContext(),
                R.array.car_years, R.layout.spinner_item);
        adapterCarYear.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarYear.setAdapter(adapterCarYear);

        //Save button functionality
        Button saveButton = rootView.findViewById(R.id.btnSave);

        saveButton.setOnClickListener(v -> {
            String make = spinnerCarMake.getSelectedItem().toString();
            String model = spinnerCarModel.getSelectedItem().toString();
            String year = spinnerCarYear.getSelectedItem().toString();
            String nickname = ((EditText) rootView.findViewById(R.id.inputNickName)).getText().toString();

            //Save the vehicle data to the Sqlite database
            VehicleDatabaseHelper dbHelper = new VehicleDatabaseHelper(getContext());
            dbHelper.addVehicle(make, model, year, nickname);

            Bundle bundle = new Bundle();
            bundle.putString("vehicleMake", make);
            bundle.putString("vehicleModel", model);
            bundle.putString("vehicleYear", year);
            bundle.putString("vehicleLicensePlate", nickname);

            NavHostFragment.findNavController(AddVehicleActivity.this).navigate(R.id.action_AddVehicleActivity_to_addnewmaint, bundle);
        });

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
        return rootView;
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