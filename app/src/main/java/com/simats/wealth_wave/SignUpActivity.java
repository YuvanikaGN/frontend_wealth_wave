package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.simats.wealth_wave.models.SignupRequest;
import com.simats.wealth_wave.responses.SignupResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText fullNameEditText, emailEditText, passwordEditText, mobileEditText;
    AppCompatButton createBtn;

    private static final String USER_PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        createBtn = findViewById(R.id.createBtn);

        createBtn.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String mobile = mobileEditText.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SignupRequest signupRequest = new SignupRequest(fullName, email, password, mobile);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<SignupResponse> call = apiService.registerUser(signupRequest);

            call.enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SignupResponse signupResponse = response.body();
                        Toast.makeText(SignUpActivity.this, signupResponse.getMessage(), Toast.LENGTH_LONG).show();

                        if ("success".equalsIgnoreCase(signupResponse.getStatus())) {
                            int userId = signupResponse.getUserId();
                            Log.d("DEBUG", "Saving user id: " + userId);

                            // Save user info but NOT logged-in yet
                            SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("user_id", userId);
                            editor.putString("email", email);
                            editor.putString("name", fullName);
                            editor.putString("mobile", mobile);
                            editor.putBoolean("isFirstTime", false);
                            // ❌ don't set isLoggedIn yet
                            editor.apply();

                            // Go to UserDetails / AccountCreated page
                            Intent intent = new Intent(SignUpActivity.this, AccCreatedActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Signup failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Optional: Status bar color
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }
}
