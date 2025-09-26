package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SecuritySettingsActivity extends AppCompatActivity {

    private ImageView backArrow;

    private ImageButton homeBtn;

    private ImageButton investmentTrackingBtn;

    private ImageButton userprofileBtn;

    private ImageButton savingsBtn;

    private AppCompatButton backToProfileBtn;

    private ImageView applockNext;

    private ImageView loginNext;

    private ImageView security;

    private ImageView  menuIcon;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.security_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Open/Close Drawer
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Navigation item selection using if-else
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) {
                startActivity(new Intent(SecuritySettingsActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(SecuritySettingsActivity.this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(SecuritySettingsActivity.this, ProgressTrackingActivity.class));
            }
//            else if (id == R.id.nav_income) {
//                startActivity(new Intent(SecuritySettingsActivity.this, IncomeActivity.class));
//            }
            else if (id == R.id.nav_transactions) {
                startActivity(new Intent(SecuritySettingsActivity.this, SecuritySettingsActivity.class));
            } else if (id == R.id.nav_edit_savings) {
                startActivity(new Intent(SecuritySettingsActivity.this, EditSavingsActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(SecuritySettingsActivity.this, LogoPageActivity.class));
                finish();
            }

            overridePendingTransition(0, 0);
            return true;
        });


        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, UserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        investmentTrackingBtn = findViewById(R.id.investmentTrackingBtn);
        investmentTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, ProgressTrackingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        savingsBtn = findViewById(R.id.savingsBtn);
        savingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, SavingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        userprofileBtn = findViewById(R.id.userprofileBtn);
        userprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, ProgressTrackingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });



        backToProfileBtn = findViewById(R.id.backToProfileBtn);
        backToProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, UserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        applockNext = findViewById(R.id.applockNext);
        applockNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, AppLockActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        loginNext = findViewById(R.id.loginNext);
        loginNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, LoginHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        security = findViewById(R.id.security);
        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecuritySettingsActivity.this, GoalStatus.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}