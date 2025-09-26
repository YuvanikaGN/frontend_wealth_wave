package com.simats.wealth_wave.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.ForgotPasswordActivity;
import com.simats.wealth_wave.HomeActivity;
import com.simats.wealth_wave.R;
import com.simats.wealth_wave.SignUpActivity;
import com.simats.wealth_wave.models.LoginRequest;
import com.simats.wealth_wave.responses.LoginResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPageActivity extends AppCompatActivity {

    private AppCompatButton loginBtn;
    private TextView forgotPassword, createAccBtn;
    private EditText emailEditText, passwordEditText;

    ApiService apiService;
    private static final String USER_PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getClient().create(ApiService.class);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPassword);
        createAccBtn = findViewById(R.id.createAccBtn);

        loginBtn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPageActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest loginRequest = new LoginRequest(email, password);

            apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        if ("success".equalsIgnoreCase(loginResponse.getStatus())) {
                            Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            int userId = loginResponse.getUserId();
                            String emailFromResponse = loginResponse.getEmail();
                            String fullName = loginResponse.getFullName();
                            String mobileFromResponse = loginResponse.getMobile(); // ✅ make sure backend sends this

                            SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = userPrefs.edit();
                            editor.putInt("user_id", userId);
                            editor.putString("email", emailFromResponse);
                            editor.putString("name", fullName);
                            editor.putString("mobile", mobileFromResponse); // ✅ save mobile here
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            Intent intent = new Intent(LoginPageActivity.this, HomeActivity.class);
                            intent.putExtra("email", emailFromResponse);
                            intent.putExtra("fullName", fullName);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginPageActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginPageActivity.this, "Server error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginPageActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        createAccBtn.setOnClickListener(v -> startActivity(new Intent(LoginPageActivity.this, SignUpActivity.class)));
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginPageActivity.this, ForgotPasswordActivity.class)));
    }
}
