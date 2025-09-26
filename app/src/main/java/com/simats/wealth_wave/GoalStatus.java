package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.simats.wealth_wave.responses.GetAllPlansResponse;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalStatus extends AppCompatActivity {

    private ImageView backArrow, menuIcon;

    private ImageButton homeBtn;

    private ImageButton investmentTrackingBtn;

    private ImageButton userprofileBtn;

    private ImageButton savingsBtn;

    private AppCompatButton backToProfileBtn;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.goal_status);
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
                startActivity(new Intent(GoalStatus.this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(GoalStatus.this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(GoalStatus.this, ProgressTrackingActivity.class));
            }
//            else if (id == R.id.nav_income) {
//                startActivity(new Intent(SecurityActivity.this, IncomeActivity.class));
//            }
            else if (id == R.id.nav_transactions) {
                startActivity(new Intent(GoalStatus.this, GoalStatus.class));
            } else if (id == R.id.nav_edit_savings) {
                startActivity(new Intent(GoalStatus.this, EditSavingsActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(GoalStatus.this, LogoPageActivity.class));
                finish();
            }

            overridePendingTransition(0, 0);
            return true;
        });

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoalStatus.this, UserProfileActivity.class);
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

        RecyclerView rvPlans = findViewById(R.id.rvPlans);
        rvPlans.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            ApiService apiService = RetrofitClient.getInstance().getApi();

            apiService.getPlansWithApprox(userId)
                    .enqueue(new Callback<GetAllPlansResponse>() {
                        @Override
                        public void onResponse(Call<GetAllPlansResponse> call, Response<GetAllPlansResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                                List<GetAllPlansResponse.Data> plans = response.body().getData();
                                rvPlans.setAdapter(new PlanAdapter(plans));
                            } else {
                                Toast.makeText(GoalStatus.this, "No plans found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetAllPlansResponse> call, Throwable t) {
                            Toast.makeText(GoalStatus.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }




        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }
}
