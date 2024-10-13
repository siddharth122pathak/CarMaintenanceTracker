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
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class AddVehicleActivity extends Fragment {

    private LottieAnimationView carAnimationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_vehicle, container, false);

        carAnimationView = rootView.findViewById(R.id.carAnimation);

        Spinner spinnerCarMake = rootView.findViewById(R.id.spinnerCarMake);
        ArrayAdapter<CharSequence> adapterCarMake = ArrayAdapter.createFromResource(getContext(),
                R.array.car_makes, R.layout.spinner_item);
        adapterCarMake.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarMake.setAdapter(adapterCarMake);

        Spinner spinnerCarModel = rootView.findViewById(R.id.spinnerCarModel);
        ArrayAdapter<CharSequence> adapterCarModel = ArrayAdapter.createFromResource(getContext(),
                R.array.car_models, R.layout.spinner_item);
        adapterCarModel.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarModel.setAdapter(adapterCarModel);

        Spinner spinnerCarYear = rootView.findViewById(R.id.spinnerCarYear);
        ArrayAdapter<CharSequence> adapterCarYear = ArrayAdapter.createFromResource(getContext(),
                R.array.car_years, R.layout.spinner_item);
        adapterCarYear.setDropDownViewResource(R.layout.spinner_item);
        spinnerCarYear.setAdapter(adapterCarYear);

        Button saveButton = rootView.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(v -> {
            String make = spinnerCarMake.getSelectedItem().toString();
            String model = spinnerCarModel.getSelectedItem().toString();
            String year = spinnerCarYear.getSelectedItem().toString();
            String nickname = ((EditText) rootView.findViewById(R.id.inputNickName)).getText().toString();
            saveVehicle(make, model, year, nickname);
        });

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

    // Method to update car animation based on selected car make
    private void updateCarAnimation(String carMake) {
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

    // Save vehicle information to the database
    private void saveVehicle(String make, String model, String year, String nickname) {
        if (make.isEmpty() || model.isEmpty() || year.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in the make, model, and year fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserVehicleApi vehicleApi = RetrofitClient.getRetrofitInstance().create(UserVehicleApi.class);
        Call<ResponseBody> call = vehicleApi.addVehicle(make, model, year, nickname.isEmpty() ? null : nickname);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Vehicle added successfully", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(AddVehicleActivity.this)
                            .navigate(R.id.action_AddVehicleActivity_to_addnewmaint);
                } else {
                    Toast.makeText(getContext(), "Failed to add vehicle", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
