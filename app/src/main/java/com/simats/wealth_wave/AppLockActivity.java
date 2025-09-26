package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AppLockActivity extends AppCompatActivity {

    private ImageView backArrow,menuIcon;

    private ImageButton homeBtn;

    private ImageButton investmentTrackingBtn;

    private ImageButton userprofileBtn;

    private ImageButton savingsBtn;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.app_lock);
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
                startActivity(new Intent(AppLockActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(AppLockActivity.this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(AppLockActivity.this, ProgressTrackingActivity.class));
            }
//            else if (id == R.id.nav_income) {
//                startActivity(new Intent(AppLockActivity.this, IncomeActivity.class));
//            }
            else if (id == R.id.nav_transactions) {
                startActivity(new Intent(AppLockActivity.this, AppLockActivity.class));
            } else if (id == R.id.nav_edit_savings) {
                startActivity(new Intent(AppLockActivity.this, EditSavingsActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(AppLockActivity.this, LogoPageActivity.class));
                finish();
            }

            overridePendingTransition(0, 0);
            return true;
        });

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppLockActivity.this, SecuritySettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppLockActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        investmentTrackingBtn = findViewById(R.id.investmentTrackingBtn);
        investmentTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppLockActivity.this, ProgressTrackingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        savingsBtn = findViewById(R.id.savingsBtn);
        savingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppLockActivity.this, SavingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        userprofileBtn = findViewById(R.id.userprofileBtn);
        userprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppLockActivity.this, ProgressTrackingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}