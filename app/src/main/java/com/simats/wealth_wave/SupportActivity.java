package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class SupportActivity extends AppCompatActivity {

    private ImageView  menuIcon;
    
    private LinearLayout accBtn, privacyBtn, appSettingsBtn, deteleAccBtn, updateBtn;


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.support);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout deleteAccBtn = findViewById(R.id.deleteAccBtn);
        TextView deleteAnswer = findViewById(R.id.deleteAnswer);
        ImageView deleteArrow = findViewById(R.id.deleteArrow);

        deleteAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteAnswer.getVisibility() == View.GONE) {
                    deleteAnswer.setVisibility(View.VISIBLE);
                    deleteArrow.setRotation(90); // Optional: rotate the arrow to indicate dropdown
                } else {
                    deleteAnswer.setVisibility(View.GONE);
                    deleteArrow.setRotation(0);  // Rotate back
                }
            }
        });

        LinearLayout updateBtn = findViewById(R.id.updateBtn);
        TextView updateAnswer = findViewById(R.id.updateAnswer);
        ImageView updateArrow = findViewById(R.id.updateArrow);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateAnswer.getVisibility() == View.GONE) {
                    updateAnswer.setVisibility(View.VISIBLE);
                    updateArrow.setRotation(90); // dropdown effect
                } else {
                    updateAnswer.setVisibility(View.GONE);
                    updateArrow.setRotation(0);
                }
            }
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
                startActivity(new Intent(SupportActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(SupportActivity.this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(SupportActivity.this, ProgressTrackingActivity.class));
            }
//            else if (id == R.id.nav_income) {
//                startActivity(new Intent(SupportActivity.this, IncomeActivity.class));
//            }
            else if (id == R.id.nav_transactions) {
                startActivity(new Intent(SupportActivity.this, SupportActivity.class));
            } else if (id == R.id.nav_edit_savings) {
                startActivity(new Intent(SupportActivity.this, EditSavingsActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(SupportActivity.this, LogoPageActivity.class));
                finish();
            }

            overridePendingTransition(0, 0);
            return true;
        });

        accBtn = findViewById(R.id.accBtn);
        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupportActivity.this, UserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        privacyBtn = findViewById(R.id.privacyBtn);
        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupportActivity.this, GoalStatus.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home_bot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home_bot) startActivity(new Intent(this, HomeActivity.class));
            if (id == R.id.nav_savings_plan_bot) startActivity(new Intent(this, SavingsActivity.class));
            else if (id == R.id.nav_investment_bot) startActivity(new Intent(this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_profile_bot) startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        });


        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }
}