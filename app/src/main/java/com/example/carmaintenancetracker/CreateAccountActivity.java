package com.example.carmaintenancetracker;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameEditText, fullNameEditText, emailEditText, phoneNumberEditText, passwordEditText;
    private Button submitButton;
    private LottieAnimationView userCreatedAnimation;
    private TextView createdText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        usernameEditText = findViewById(R.id.usernameEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        submitButton = findViewById(R.id.submitButton);

        userCreatedAnimation = findViewById(R.id.userCreatedAnimation);
        createdText = findViewById(R.id.createdText);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUsernameAndCreateAccount();
            }
        });
    }

    // Function to check if the username already exists
    private void checkUsernameAndCreateAccount() {
        String username = usernameEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid phone number");
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number
        if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d{10}")) {
            phoneNumberEditText.setError("Enter a valid phone number");
            Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!username.isEmpty() && !fullName.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty() && !password.isEmpty()) {
            UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

            // Call to check if the username already exists in the database (ignoring password check here)
            Call<ResponseBody> checkUserCall = userApi.checkUsername(username);  // We will create a new API call for just checking the username
            checkUserCall.enqueue(new Callback<ResponseBody>() {
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
                                    Toast.makeText(CreateAccountActivity.this, "Username already exists, please choose another", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Proceed to create the new account if the username does not exist
                                    createNewAccount(username, fullName, email, phoneNumber, password);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CreateAccountActivity.this, "Response parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Error: Invalid response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CreateAccountActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewAccount(String username, String fullName, String email, String phoneNumber, String password) {
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);
        Call<ResponseBody> call = userApi.createUser(username, fullName, email, phoneNumber, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CreateAccount", "Response: " + response.body().toString());
                    Intent intent = new Intent(CreateAccountActivity.this, UserCreatedAnimationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("CreateAccount", "Failed to create account: " + response.errorBody());
                    Toast.makeText(CreateAccountActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CreateAccount", "API Call Failure: " + t.getMessage());
                Toast.makeText(CreateAccountActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}