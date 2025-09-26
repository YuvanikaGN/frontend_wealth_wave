package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.models.OTPRequest;
import com.simats.wealth_wave.responses.OTPResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private AppCompatButton continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_password);

        // Bind Views
        emailInput = findViewById(R.id.emailInput);         // Make sure this ID exists in XML
        continueBtn = findViewById(R.id.continueBtn);       // Make sure this ID exists in XML

        // Handle Insets for immersive UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle Button Click
        continueBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show immediate feedback
            Toast.makeText(ForgotPasswordActivity.this, "Sending mail...", Toast.LENGTH_SHORT).show();

            OTPRequest request = new OTPRequest(email);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<OTPResponse> call = apiService.sendOTP(request);

            call.enqueue(new Callback<OTPResponse>() {
                @Override
                public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        OTPResponse res = response.body();
                        if (res.isStatus()) {
                            Toast.makeText(ForgotPasswordActivity.this, "OTP sent to your email", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgotPasswordActivity.this, OtpActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("otp", res.getOtp()); // optional
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OTPResponse> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }
}
