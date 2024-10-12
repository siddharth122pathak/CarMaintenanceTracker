package com.example.carmaintenancetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{

    private EditText usernameEditText, passwordEditText;
    private Button signInButton, createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        // Add a log statement to verify the method is called
        Toast.makeText(this, "Attempting login...", Toast.LENGTH_SHORT).show();

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);
            Call<ResponseBody> call = userApi.checkUser(username, password);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseString = response.body().string();
                            System.out.println(responseString);
                            System.out.println("Response: " + responseString);

                            // Remove the "Connected successfully to the database" message if it exists
                            if (responseString.startsWith("Connected successfully to the database!")) {
                                responseString = responseString.replace("Connected successfully to the database!", "").trim();
                            }

                            // Now, process the remaining JSON string
                            if (responseString.trim().startsWith("{")) {
                                JSONObject jsonResponse = new JSONObject(responseString);
                                if (jsonResponse.getString("status").equals("success")) {
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle plain string response
                                Toast.makeText(LoginActivity.this, "Server Response: " + responseString, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Response parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: Invalid response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
        }
    }
}