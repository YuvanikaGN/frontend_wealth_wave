package com.simats.wealth_wave;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.simats.wealth_wave.models.UserIdRequest;
import com.simats.wealth_wave.responses.GetAllPlansResponse;
import com.simats.wealth_wave.responses.GetSavingsResponse;
import com.simats.wealth_wave.responses.GetUserDetailsResponse;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.retrofit.RetrofitClient;
import com.simats.wealth_wave.ui.LoginPageActivity;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private TextView tvFullName, tvIncome, tvTargetAmount, tvGoal, goalAmount,
            tvSavedAmount, tvProgress, tvMonthsRemaining, tvApproxMoney;

    private ImageView menuIcon, notification;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private ProgressBar progressBar;

    private AppCompatButton btnViewPlans;

    private static final String USER_PREFS_NAME = "UserPrefs";
    private static final String PREF_NAME = "WealthWavePrefs";
    private LinearLayout nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Handle system bars properly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind views
        tvFullName = findViewById(R.id.tvFullName);
        tvIncome = findViewById(R.id.tvIncome);
        tvTargetAmount = findViewById(R.id.tvTargetAmount);
        tvGoal = findViewById(R.id.tvGoal);
        goalAmount = findViewById(R.id.goalAmount);
        tvSavedAmount = findViewById(R.id.tvSavedAmount);
        tvProgress = findViewById(R.id.tvProgress);
        tvMonthsRemaining = findViewById(R.id.tvMonthsRemaining);
        tvApproxMoney = findViewById(R.id.tvApproxMoney);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);
        progressBar = findViewById(R.id.progressBar);
        notification = findViewById(R.id.notification);
        btnViewPlans = findViewById(R.id.btnViewPlans);

        // Display logged-in user's name/email
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "User Name");
        String email = prefs.getString("email", "user@gmail.com");
        tvFullName.setText(name);

        View headerView = navigationView.getHeaderView(0);

        // ✅ Add this line for profile photo
        de.hdodenhof.circleimageview.CircleImageView navUserPhoto =
                headerView.findViewById(R.id.nav_user_photo);

// Load saved image URL from prefs
        int userId = prefs.getInt("user_id", 0); // get currently logged-in user ID
        String userProfileKey = "profileImageUrl_" + userId; // key unique to this user
        String profileImageUrl = prefs.getString(userProfileKey, ""); // fetch per-user image
        if (!profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.user_grad)
                    .into(navUserPhoto);
        }

        LinearLayout nav_view = headerView.findViewById(R.id.nav_view);
        nav_view.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        navUserName.setText(name);
        navUserEmail.setText(email);

        // Load selected plan on launch
        loadSelectedPlan();

        // Button to select/view plans
        btnViewPlans.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(HomeActivity.this, "User ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitClient.getInstance().getApi().getPlansWithApprox(userId)
                    .enqueue(new Callback<GetAllPlansResponse>() {
                        @Override
                        public void onResponse(Call<GetAllPlansResponse> call, Response<GetAllPlansResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                                List<GetAllPlansResponse.Data> plans = response.body().getData();
                                if (plans.isEmpty()) {
                                    Toast.makeText(HomeActivity.this, "No plans found", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String[] planTitles = new String[plans.size()];
                                for (int i = 0; i < plans.size(); i++)
                                    planTitles[i] = "🎯 " + plans.get(i).getGoal();

                                new AlertDialog.Builder(HomeActivity.this)
                                        .setTitle("Select a Goal")
                                        .setItems(planTitles, (dialog, which) -> {
                                            GetAllPlansResponse.Data selectedPlan = plans.get(which);

                                            saveSelectedGoalToPrefs(
                                                    selectedPlan.getId(),
                                                    selectedPlan.getGoal(),
                                                    selectedPlan.getIncome(),
                                                    selectedPlan.getTarget_amount(),
                                                    selectedPlan.getApprox_money(),
                                                    selectedPlan.getDuration(),
                                                    userId
                                            );
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();

                            } else {
                                Toast.makeText(HomeActivity.this, "No plans found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetAllPlansResponse> call, Throwable t) {
                            Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Drawer toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home_bot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home_bot) return true;
            if (id == R.id.nav_savings_plan_bot) startActivity(new Intent(this, SavingsActivity.class));
            else if (id == R.id.nav_investment_bot) startActivity(new Intent(this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_profile_bot) startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        });

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_savings_plan) startActivity(new Intent(HomeActivity.this, SavingsActivity.class));
            else if (id == R.id.nav_investment) startActivity(new Intent(HomeActivity.this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_transactions) startActivity(new Intent(HomeActivity.this, TransactionsActivity.class));
            else if (id == R.id.nav_edit_savings) startActivity(new Intent(HomeActivity.this, EditSavingsActivity.class));
            else if (id == R.id.nav_chatbot) startActivity(new Intent(HomeActivity.this, ChatbotActivity.class));
            else if (id == R.id.nav_logout) {
                startActivity(new Intent(HomeActivity.this, LoginPageActivity.class));
                finish();
            }
            overridePendingTransition(0, 0);
            return true;
        });
        notification.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
            overridePendingTransition(0, 0);
        });

        // Status & navigation bars
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }

    // ------------------ Plan Save & Load -------------------
    private void saveSelectedGoalToPrefs(int planId, String goalTitle, double income, double targetAmount,
                                         double approxMoney, String duration, int userId) {
        SharedPreferences planPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        saveDouble(planPrefs, "selected_plan_income", income);
        saveDouble(planPrefs, "selected_plan_target", targetAmount);
        saveDouble(planPrefs, "selected_plan_approx", approxMoney);
        planPrefs.edit()
                .putInt("selected_plan_id", planId)
                .putString("selected_plan_goal", goalTitle)
                .putString("selected_plan_duration", duration)
                .putInt("plan_owner_user_id", userId)
                .apply();

        // ⚡ Don't reset saved amount blindly, each user+plan combo keeps its own progress
        refreshHomeProgressUI();
    }

    private void loadSelectedPlan() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int selectedPlanId = prefs.getInt("selected_plan_id", -1);
        int ownerUserId = prefs.getInt("plan_owner_user_id", -1);

        int currentUserId = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE).getInt("user_id", -1);

        if (selectedPlanId == -1 || ownerUserId != currentUserId) {
            prefs.edit().clear().apply();
            if (currentUserId != -1) fetchGoalOverview(currentUserId);
            return;
        }

        refreshHomeProgressUI();
    }

    private double getPlanSavedAmount(int planId) {
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        try {
            return Double.longBitsToDouble(
                    userPrefs.getLong("total_saved_" + planId, Double.doubleToLongBits(0))
            );
        } catch (ClassCastException e) {
            float oldValue = userPrefs.getFloat("total_saved_" + planId, 0f);
            userPrefs.edit().putLong("total_saved_" + planId, Double.doubleToRawLongBits(oldValue)).apply();
            return oldValue;
        }
    }

    private void saveDouble(SharedPreferences prefs, String key, double value) {
        prefs.edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    private double getSafeDouble(SharedPreferences prefs, String key) {
        try {
            return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(0)));
        } catch (ClassCastException e) {
            float oldValue = prefs.getFloat(key, 0f);
            prefs.edit().putLong(key, Double.doubleToRawLongBits(oldValue)).apply();
            return oldValue;
        }
    }

    // ------------------ Refresh UI -------------------
    private void refreshHomeProgressUI() {
        SharedPreferences planPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);

        int selectedPlanId = planPrefs.getInt("selected_plan_id", -1);
        int userId = userPrefs.getInt("user_id", -1);

        if (selectedPlanId == -1 || userId == -1) return;

        double income = getSafeDouble(planPrefs, "selected_plan_income");
        double target = getSafeDouble(planPrefs, "selected_plan_target");
        double approx = getSafeDouble(planPrefs, "selected_plan_approx");
        String goal = planPrefs.getString("selected_plan_goal", "Your Goal");
        String duration = planPrefs.getString("selected_plan_duration", "-");

        // 1️⃣ Call backend to fetch total savings
        RetrofitClient.getInstance().getApi().getSavings(userId, selectedPlanId)
                .enqueue(new Callback<GetSavingsResponse>() {
                    @Override
                    public void onResponse(Call<GetSavingsResponse> call, Response<GetSavingsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                            double totalSaved = response.body().getTotalSavings();

                            // Save in SharedPreferences for cache/offline
                            userPrefs.edit()
                                    .putLong("total_saved_" + selectedPlanId,
                                            Double.doubleToRawLongBits(totalSaved))
                                    .apply();

                            // Update UI
                            tvGoal.setText(goal);
                            tvIncome.setText("₹ " + String.format("%.2f", income));
                            tvTargetAmount.setText("₹ " + String.format("%.2f", target));
                            goalAmount.setText("🎯 Goal - ₹ " + String.format("%.2f", target));
                            tvMonthsRemaining.setText(duration);
                            tvApproxMoney.setText("₹ " + String.format("%.2f", approx));
                            tvSavedAmount.setText("Saved - ₹ " + String.format("%.2f", totalSaved));

                            int percent = target > 0 ? (int)((totalSaved / target) * 100) : 0;
                            if (percent > 100) percent = 100;

                            ValueAnimator animator = ValueAnimator.ofInt(progressBar.getProgress(), percent);
                            animator.setDuration(500);
                            animator.addUpdateListener(animation -> {
                                int value = (int) animation.getAnimatedValue();
                                progressBar.setProgress(value);
                                tvProgress.setText(value + "% reached");
                            });
                            animator.start();

                        } else {
                            Toast.makeText(HomeActivity.this, "Failed to fetch savings", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSavingsResponse> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHomeProgressUI();
    }

    // ---------------- Fetch goal overview from backend ----------------
    private void fetchGoalOverview(int userId) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.24.93.232/app_database/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getUserDetails(new UserIdRequest(userId)).enqueue(new Callback<GetUserDetailsResponse>() {
            @Override
            public void onResponse(Call<GetUserDetailsResponse> call, Response<GetUserDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    GetUserDetailsResponse data = response.body();
                    List<GetUserDetailsResponse.Goal> goals = data.getGoals();
                    if (goals != null && !goals.isEmpty()) {
                        GetUserDetailsResponse.Goal latestGoal = goals.get(0);

                        tvIncome.setText("₹ " + String.format("%.2f", latestGoal.getIncome()));
                        tvTargetAmount.setText("₹ " + String.format("%.2f", latestGoal.getTarget_amount()));
                        tvGoal.setText(latestGoal.getGoal());
                        goalAmount.setText("🎯 Goal - ₹ " + String.format("%.2f", latestGoal.getTarget_amount()));
                        tvMonthsRemaining.setText(data.getTimelineMonths() + " months");
                        tvApproxMoney.setText("₹ " + String.format("%.2f", latestGoal.getApprox_money()));

                        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        saveDouble(prefs, "goal_income_" + userId, latestGoal.getIncome());
                        prefs.edit().putString("goal_name_" + userId, latestGoal.getGoal()).apply();
                        saveDouble(prefs, "goal_approx_" + userId, latestGoal.getApprox_money());

                        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
                        saveDouble(userPrefs, "target_amount", latestGoal.getTarget_amount());

                        refreshHomeProgressUI();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "No goal data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserDetailsResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
