package com.example.carmaintenancetracker;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import okhttp3.ResponseBody;
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
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {
        String username = usernameEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        Log.d("CreateAccount", "Username: " + username + ", Name: " + fullName + ", Email: " + email + ", Phone: " + phoneNumber + ", Password: " + password);

        if (!username.isEmpty() && !fullName.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty() && !password.isEmpty()) {
            UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);
            Call<ResponseBody> call = userApi.createUser(username, fullName, email, phoneNumber, password);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("CreateAccount", "Response: " + response.body().toString());
                        Toast.makeText(CreateAccountActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        showUserCreatedAnimation(); // Close activity after successful account creation
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
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Show the animation and hide submit button after user is created successfully
    private void showUserCreatedAnimation() {
        // Hide the submit button and form
        submitButton.setVisibility(View.GONE);
        usernameEditText.setVisibility(View.GONE);
        fullNameEditText.setVisibility(View.GONE);
        emailEditText.setVisibility(View.GONE);
        phoneNumberEditText.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);

        // Show and play the animation
        userCreatedAnimation.setVisibility(View.VISIBLE);
        createdText.setVisibility(View.VISIBLE);
        userCreatedAnimation.playAnimation();  // Start animation

        // Optional: You can finish the activity after the animation ends
        userCreatedAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // You can handle the start of the animation if needed
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Finish the activity or return to login after the animation ends
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Handle animation cancellation if needed
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Handle animation repetition if needed
            }
        });
    }
}