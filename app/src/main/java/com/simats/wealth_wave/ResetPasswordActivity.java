package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.models.ResetPasswordRequest;
import com.simats.wealth_wave.responses.ResetPasswordResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.ui.LoginPageActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailInput, newPasswordInput;
    private Button resetBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resetPasswordLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.emailInput);
        newPasswordInput = findViewById(R.id.newPassword);
        resetBtn = findViewById(R.id.continueBtn);
        loginBtn = findViewById(R.id.loginBtn);

        // Hide loginBtn initially
        loginBtn.setVisibility(View.GONE);

        // Pre-fill email if passed via Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user_email")) {
            String passedEmail = intent.getStringExtra("user_email");
            if (passedEmail != null) {
                emailInput.setText(passedEmail);
            }
        }

        resetBtn.setOnClickListener(v -> resetPassword());

        loginBtn.setOnClickListener(v -> {
            // Navigate to login activity
            Intent loginIntent = new Intent(ResetPasswordActivity.this, LoginPageActivity.class);
            startActivity(loginIntent);
            finish();
        });
    }

    private void resetPassword() {
        String email = emailInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();

        if (email.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Email and new password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest(email, newPassword);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResetPasswordResponse> call = apiService.resetPassword(request);

        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    // ✅ Show login button now
                    loginBtn.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
