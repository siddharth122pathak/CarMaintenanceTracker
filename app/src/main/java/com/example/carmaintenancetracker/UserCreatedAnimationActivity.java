package com.example.carmaintenancetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class UserCreatedAnimationActivity extends AppCompatActivity {

    private LottieAnimationView userCreatedAnimation;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_animation);

        userCreatedAnimation = findViewById(R.id.userCreatedAnimation);
        signInButton = findViewById(R.id.signInButton);

        // Start the animation
        userCreatedAnimation.playAnimation();

        // Set a click listener on the button to navigate to the login page
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCreatedAnimationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the animation activity
            }
        });
    }
}