package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerificationActivity extends AppCompatActivity {

    private static final int DELAY_MILLISECONDS = 3000; // Total duration
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.verification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.verificationProgressBar); // match with your XML ID
        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Animate progress gradually
        animateProgressBar();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(VerificationActivity.this, BankaccVerificationActivity.class);
            startActivity(intent);
            finish();
        }, DELAY_MILLISECONDS);
    }

    private void animateProgressBar() {
        Handler handler = new Handler(Looper.getMainLooper());
        int delay = 30; // ms between each step
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
