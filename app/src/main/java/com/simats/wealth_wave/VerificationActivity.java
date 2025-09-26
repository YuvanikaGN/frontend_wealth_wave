package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.responses.BankVerifyResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private static final int DELAY_MILLISECONDS = 3000; // 3 seconds
    private ProgressBar progressBar;

    private String name, bankName, accNumber, ifscCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.verificationProgressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Get bank details from previous screen
        name = getIntent().getStringExtra("name");
        bankName = getIntent().getStringExtra("bank_name");
        accNumber = getIntent().getStringExtra("acc_number");
        ifscCode = getIntent().getStringExtra("ifsc_code");

        animateProgressBar();
    }



    private void animateProgressBar() {
        Handler handler = new Handler(Looper.getMainLooper());
        int delay = 30;
        int totalSteps = DELAY_MILLISECONDS / delay;
        int[] progress = {0};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= 100) {
                    progressBar.setProgress(progress[0]);
                    progress[0]++;
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.post(runnable);
    }
}
