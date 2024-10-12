package com.example.carmaintenancetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button signInButton, createAccountButton;
    private TextView forgotPasswordTextView;  // New TextView for Forgot Password

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);  // Initialize the new TextView

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

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    }

    private void attemptLogin() {
        // Existing login logic remains unchanged
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

                            if (responseString.startsWith("Connected successfully to the database!")) {
                                responseString = responseString.replace("Connected successfully to the database!", "").trim();
                            }

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

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter your registered email");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                if (!email.isEmpty()) {
                    verifyEmailAndResetPassword(email);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showNewPasswordDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Password");

        // Set up the input for the new password
        final EditText input = new EditText(this);
        input.setHint("Enter your new password");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString().trim();
                if (!newPassword.isEmpty()) {
                    // Call the resetPassword method if the new password is not empty
                    resetPassword(email, newPassword);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void verifyEmailAndResetPassword(String email) {
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);
        Call<ResponseBody> call = userApi.verifyEmail(email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        System.out.println(responseString);

                        if (responseString.startsWith("Connected successfully to the database!")) {
                            responseString = responseString.replace("Connected successfully to the database!", "").trim();
                        }

                        if (responseString.trim().startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);
                            if (jsonResponse.getString("status").equals("success")) {
                                Toast.makeText(LoginActivity.this, "Email Found", Toast.LENGTH_SHORT).show();
                                showNewPasswordDialog(email);
                            } else {
                                Toast.makeText(LoginActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
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
    }

    private void resetPassword(String email, String newPassword) {
        // Call your backend API to reset the password for the given email
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);
        Call<ResponseBody> call = userApi.resetPassword(email, newPassword);  // Assuming you have an endpoint to reset password

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        System.out.println("Response: " + responseString);

                        if (responseString.startsWith("Connected successfully to the database!")) {
                            responseString = responseString.replace("Connected successfully to the database!", "").trim();
                        }

                        // Process the JSON response to check if the password was reset successfully
                        if (responseString.trim().startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(responseString);
                            if (jsonResponse.getString("status").equals("success")) {
                                Toast.makeText(LoginActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
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
    }
}
