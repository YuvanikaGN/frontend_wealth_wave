package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.ui.LoginPageActivity;

public class LogoPageActivity extends AppCompatActivity {

    private static final String USER_PREFS_NAME = "UserPrefs";

    private AppCompatButton getStartedBtn;
    private TextView loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.logo_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getStartedBtn = findViewById(R.id.getStartedBtn);
        loginBtn = findViewById(R.id.loginBtn);

        // SharedPreferences check
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstTime", true);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        boolean hasAccount = prefs.contains("user_id"); // checks if user signed up

        if (!isFirstTime) {
            // Returning user → auto redirect
            if (isLoggedIn) {
                startActivity(new Intent(LogoPageActivity.this, HomeActivity.class));
            } else if (hasAccount) {
                startActivity(new Intent(LogoPageActivity.this, LoginPageActivity.class));
            } else {
                startActivity(new Intent(LogoPageActivity.this, SignUpActivity.class));
            }
            finish();
        } else {
            // First-time users → show buttons
            getStartedBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);

            getStartedBtn.setOnClickListener(v -> {
                startActivity(new Intent(LogoPageActivity.this, SignUpActivity.class));
                prefs.edit().putBoolean("isFirstTime", false).apply();
            });

            loginBtn.setOnClickListener(v -> {
                startActivity(new Intent(LogoPageActivity.this, LoginPageActivity.class));
            });
        }
    }
}
