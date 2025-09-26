package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class BankaccVerificationActivity extends AppCompatActivity {

    private TextView tvHolderName, tvBankName, tvAccNumber;

    private AppCompatButton continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bankacc_verification);

        // Link to correct IDs from XML (you must update your XML accordingly)
        tvHolderName = findViewById(R.id.tvHolderName);
        tvBankName = findViewById(R.id.tvBankName);
        tvAccNumber = findViewById(R.id.tvAccNumber);

        // Get data from intent
        String name = getIntent().getStringExtra("name");
        String bankName = getIntent().getStringExtra("bank_name");
        String accNumber = getIntent().getStringExtra("acc_number");

        tvHolderName.setText(name);
        tvBankName.setText(bankName);
        tvAccNumber.setText(accNumber);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(v -> {
            startActivity(new Intent(BankaccVerificationActivity.this, UserDetailsActivity.class));

        });
    }
}
