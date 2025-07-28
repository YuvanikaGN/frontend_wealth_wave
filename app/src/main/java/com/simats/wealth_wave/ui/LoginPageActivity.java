package com.simats.wealth_wave.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.ForgotPasswordActivity;
import com.simats.wealth_wave.HomeActivity;
import com.simats.wealth_wave.LogoPageActivity;
import com.simats.wealth_wave.MainActivity;
import com.simats.wealth_wave.MobileActivity;
import com.simats.wealth_wave.R;
import com.simats.wealth_wave.SignInActivity;

public class LoginPageActivity extends AppCompatActivity {

    private Button loginBtn;

    private ImageView backArrow;

    private TextView forgotPassword;

    private TextView createAccBtn;

    private AppCompatButton mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPageActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPageActivity.this, LogoPageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPageActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        createAccBtn = findViewById(R.id.createAccBtn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPageActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        mobile = findViewById(R.id.mobile);
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPageActivity.this, MobileActivity.class);
                startActivity(intent);
            }
        });


    }
}