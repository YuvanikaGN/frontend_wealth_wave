package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.simats.wealth_wave.models.BankVerifyRequest;
import com.simats.wealth_wave.responses.BankVerifyResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailsActivity extends AppCompatActivity {

    private AppCompatButton continueBtn;
    private EditText etBankName, etName, etAccNumber, etIfscCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_details);

//        etBankName = findViewById(R.id.etBankName);
//        etName = findViewById(R.id.etName);
        etAccNumber = findViewById(R.id.etAccNumber);
        etIfscCode = findViewById(R.id.etIfscCode);
        continueBtn = findViewById(R.id.continueBtn);

        continueBtn.setOnClickListener(v -> {
            String accNumber = etAccNumber.getText().toString().trim();
            String ifscCode = etIfscCode.getText().toString().trim();

            if (accNumber.isEmpty() || ifscCode.isEmpty()) {
                Toast.makeText(BankDetailsActivity.this, "Please enter Account Number & IFSC", Toast.LENGTH_SHORT).show();
                return;
            }

            BankVerifyRequest request = new BankVerifyRequest(accNumber, ifscCode);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            Call<BankVerifyResponse> call = apiService.verifyBankDetails(request);
            call.enqueue(new Callback<BankVerifyResponse>() {
                @Override
                public void onResponse(Call<BankVerifyResponse> call, Response<BankVerifyResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BankVerifyResponse res = response.body();
                        if ("success".equals(res.getStatus())) {
                            Map<String, String> data = res.getData();

                            Intent intent = new Intent(BankDetailsActivity.this, BankaccVerificationActivity.class);
                            intent.putExtra("name", data.get("name"));
                            intent.putExtra("bank_name", data.get("bank_name"));
                            intent.putExtra("acc_number", data.get("acc_number"));
                            startActivity(intent);
                        } else {
                            Toast.makeText(BankDetailsActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BankDetailsActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BankVerifyResponse> call, Throwable t) {
                    Toast.makeText(BankDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }
}
