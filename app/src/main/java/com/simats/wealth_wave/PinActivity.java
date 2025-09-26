package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PinActivity extends AppCompatActivity {

    private static final String CORRECT_PIN = "9090"; // Hardcoded PIN for now

    private StringBuilder enteredPin = new StringBuilder();
    private List<View> pinDots = new ArrayList<>();

    private LinearLayout fingerprintOption;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin);

        backArrow = findViewById(R.id.backArrow);
        fingerprintOption = findViewById(R.id.fingerprintOption);

        pinDots.add(findViewById(R.id.dot1));
        pinDots.add(findViewById(R.id.dot2));
        pinDots.add(findViewById(R.id.dot3));
        pinDots.add(findViewById(R.id.dot4));

        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(PinActivity.this, PasswordActivity.class));
            overridePendingTransition(0, 0);
        });

        fingerprintOption.setOnClickListener(v -> {
            startActivity(new Intent(PinActivity.this, FingerprintActivity.class));
        });

        setupKeypad();

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar)); // replace with your desired color

    }

    private void setupKeypad() {
        ViewGroup keypad = findViewById(R.id.keypad);

        for (int i = 0; i < keypad.getChildCount(); i++) {
            View child = keypad.getChildAt(i);
            if (child instanceof Button) {
                Button numberBtn = (Button) child;
                numberBtn.setOnClickListener(v -> {
                    if (enteredPin.length() < 4) {
                        enteredPin.append(numberBtn.getText().toString());
                        updateDots();
                    }
                });
            } else if (child instanceof ImageButton) {
                ImageButton imageButton = (ImageButton) child;
                String description = (String) imageButton.getContentDescription();

                if ("Tick".equals(description)) {
                    imageButton.setOnClickListener(v -> verifyPin());
                } else if ("Delete".equals(description)) {
                    imageButton.setOnClickListener(v -> {
                        if (enteredPin.length() > 0) {
                            enteredPin.deleteCharAt(enteredPin.length() - 1);
                            updateDots();
                        }
                    });
                }
            }
        }
    }

    private void updateDots() {
        for (int i = 0; i < 4; i++) {
            View dot = pinDots.get(i);
            dot.setBackgroundResource(i < enteredPin.length() ? R.drawable.dot_filled : R.drawable.dot_empty);
        }
    }

    private void verifyPin() {
        if (enteredPin.toString().equals(CORRECT_PIN)) {
            Toast.makeText(this, "PIN correct!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PinActivity.this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            enteredPin.setLength(0);
            updateDots();
        }
    }


}
