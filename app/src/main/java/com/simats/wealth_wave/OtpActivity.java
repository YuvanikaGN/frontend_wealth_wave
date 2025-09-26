package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class OtpActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    Button verifyOtpBtn;

    String correctOtp, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);

        correctOtp = getIntent().getStringExtra("otp");
        userEmail = getIntent().getStringExtra("email");

        // Just in case, log the OTP (remove later in production)
        Log.d("Received OTP", "OTP: " + correctOtp);

        setupOTPInputs();

        verifyOtpBtn.setOnClickListener(v -> {
            String enteredOtp = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString();

            if (enteredOtp.length() < 4) {
                Toast.makeText(this, "Please enter full OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredOtp.equals(correctOtp)) {
                Toast.makeText(this, "OTP verified successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", userEmail);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                Log.d("OTP_Mismatch", "Entered: " + enteredOtp + " | Expected: " + correctOtp);
            }
        });
    }



    private void setupOTPInputs() {
        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, null));
    }

    public static class GenericTextWatcher implements TextWatcher {
        private final EditText currentView;
        private final EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }

    }

}
