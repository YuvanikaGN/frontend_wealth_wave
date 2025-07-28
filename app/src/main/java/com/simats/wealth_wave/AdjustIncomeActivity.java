package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdjustIncomeActivity extends AppCompatActivity {

    private ImageView backArrow;

    private AppCompatButton cancelBtn;

    private ImageButton homeBtn;

    private ImageButton savingsBtn;

    private ImageButton investmentTrackingBtn;

    private ImageButton userprofileBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adjust_income);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdjustIncomeActivity.this, SavingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdjustIncomeActivity.this, SavingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdjustIncomeActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        savingsBtn = findViewById(R.id.savingsBtn);
        savingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdjustIncomeActivity.this, SavingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        investmentTrackingBtn = findViewById(R.id.investmentTrackingBtn);
        investmentTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdjustIncomeActivity.this, InvestmentTrackingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        userprofileBtn = findViewById(R.id.userprofileBtn);
        userprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdjustIncomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}