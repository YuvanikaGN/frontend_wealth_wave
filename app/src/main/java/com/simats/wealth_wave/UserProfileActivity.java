package com.simats.wealth_wave;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.simats.wealth_wave.ui.LoginPageActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvfuName, tvEmail, tvActiveGoals, tvSaved;
    private TextView fundTitle, fundTarget, progressPercentageText,viewAll;
    private ProgressBar profileProgressBar;
    private ImageView editprofileBtn, menuIcon;
    private LinearLayout statements, security, support, share;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    SharedPreferences prefs;

    private OkHttpClient client;
    private static final String URL = "http://10.24.93.232/app_database/get_user_details.php";

    private SharedPreferences.OnSharedPreferenceChangeListener progressListener;

    private static final String USER_PREFS_NAME = "UserPrefs";
    private static final String PREF_NAME = "WealthWavePrefs";
    private static final String KEY_PROGRESS = "progress";
    private static final String KEY_TARGET_AMOUNT = "selected_plan_target";
    private static final String KEY_GOAL = "selected_plan_goal";
    private static final String KEY_TOTAL_SAVED_AMOUNT_PREFIX = "total_saved_amount_";

    private double totalSaved = 0;
    private double target = 0;
    private int selectedPlanId = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE); // ✅ initialize here

        ImageView userPhoto = findViewById(R.id.userPhoto);

        int userId = prefs.getInt("user_id", 0); // get currently logged-in user ID
        String userProfileKey = "profileImageUrl_" + userId; // key unique to this user
        String profileImageUrl = prefs.getString(userProfileKey, ""); // fetch per-user image
        if (!profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.user_grad)
                    .into(userPhoto);
        }

        // --- Initialize views ---
        tvfuName = findViewById(R.id.tvfuName);
        tvEmail = findViewById(R.id.tvEmail);
        tvActiveGoals = findViewById(R.id.activeGoals);
        tvSaved = findViewById(R.id.tvSaved);

        fundTitle = findViewById(R.id.fundTitle);
        fundTarget = findViewById(R.id.fundTarget);
        progressPercentageText = findViewById(R.id.progressPercentageText);
        profileProgressBar = findViewById(R.id.progressBar);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);

        statements = findViewById(R.id.statements);
        security = findViewById(R.id.security);
        support = findViewById(R.id.support);
        share = findViewById(R.id.share);
        viewAll = findViewById(R.id.viewAll);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile_bot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home_bot) startActivity(new Intent(this, HomeActivity.class));
            if (id == R.id.nav_savings_plan_bot) startActivity(new Intent(this, SavingsActivity.class));
            else if (id == R.id.nav_investment_bot) startActivity(new Intent(this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_profile_bot) startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        });

        client = new OkHttpClient();

        // --- Status bar color ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));
        }

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) {
                startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(UserProfileActivity.this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(UserProfileActivity.this, ProgressTrackingActivity.class));
            } else if (id == R.id.nav_transactions) {
                startActivity(new Intent(UserProfileActivity.this, TransactionsActivity.class));
            } else if (id == R.id.nav_edit_savings) {
                startActivity(new Intent(UserProfileActivity.this, EditSavingsActivity.class));
            } else if (id == R.id.nav_chatbot) {
                startActivity(new Intent(UserProfileActivity.this, ChatbotActivity.class));
            } else if (id == R.id.nav_logout) {

                // Go back to login screen
                Intent intent = new Intent(UserProfileActivity.this, LoginPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            overridePendingTransition(0, 0);
            return true;
        });


        // --- Drawer header update ---
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);

        // ✅ Add this line for profile photo
        de.hdodenhof.circleimageview.CircleImageView navUserPhoto =
                headerView.findViewById(R.id.nav_user_photo);

// Load saved image URL from prefs
        if (!profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_profile)
                    .into(navUserPhoto);
        }

        LinearLayout nav_view = headerView.findViewById(R.id.nav_view);
        nav_view.setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "User Name");
        String email = prefs.getString("email", "user@gmail.com");
        navUserName.setText(name);
        navUserEmail.setText(email);
        tvfuName.setText(name);
        tvEmail.setText(email);

        // --- Edit profile click ---
        editprofileBtn = findViewById(R.id.editprofileBtn);
        editprofileBtn.setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        viewAll.setOnClickListener(v -> {
//            SharedPreferences prefs = getSharedPreferences("WealthWavePrefs", MODE_PRIVATE);
            if (userId != -1) {
                Intent intent = new Intent(UserProfileActivity.this, AllPlansActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });

        statements.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, SavingsSummaryActivity.class)));
        overridePendingTransition(0, 0);
        security.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, GoalStatus.class)));
        support.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, SupportActivity.class)));
        share.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, SavingsSummaryActivity.class)));

        // --- Load data ---
        loadProfileDataFromPrefs();
        fetchUserProfile();

        // --- Listen for progress changes dynamically ---
        progressListener = (sharedPreferences, key) -> {
            if (key.startsWith("total_saved_") || key.equals(KEY_PROGRESS) || key.equals(KEY_TARGET_AMOUNT) || key.equals(KEY_GOAL)) {
                loadProfileDataFromPrefs();
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(progressListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update user info
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "User Name");
        String email = prefs.getString("email", "user@gmail.com");

        tvfuName.setText(name);
        tvEmail.setText(email);

        // Update plan progress
        loadProfileDataFromPrefs();
    }



    private void loadProfileDataFromPrefs() {
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences planPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Get selected plan
        selectedPlanId = planPrefs.getInt("selected_plan_id", -1);
        if (selectedPlanId == -1) return;

        // Load target and total saved
        target = getSafeDouble(planPrefs, "selected_plan_target");
        totalSaved = getSafeDouble(userPrefs, "total_saved_" + selectedPlanId);

        // Update goal title
        String goalTitle = planPrefs.getString("selected_plan_goal", "—");
        fundTitle.setText(goalTitle);

        fundTarget.setText(target > 0 ? "Target: ₹ " + String.format("%,.0f", target) : "Target: —");
        tvSaved.setText("₹" + String.format("%,.0f", totalSaved));

        // Calculate percentage
        int percentage = target > 0 ? (int) ((totalSaved / target) * 100) : 0;
        if (percentage > 100) percentage = 100;

        // Animate progress bar
        profileProgressBar.setMax(100);
        ValueAnimator animator = ValueAnimator.ofInt(profileProgressBar.getProgress(), percentage);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            int val = (int) animation.getAnimatedValue();
            profileProgressBar.setProgress(val);
            progressPercentageText.setText(val + "%");
            TextView tvProgressTop = findViewById(R.id.tvProgress);
            if (tvProgressTop != null) tvProgressTop.setText(val + "%");
        });
        animator.start();
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



    private void fetchUserProfile() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) return;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(UserProfileActivity.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) return;
                    String resStr = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(resStr);
//                            tvfuName.setText(json.optString("name", "User Name"));
//                            tvEmail.setText(json.optString("email", "user@gmail.com"));
                            tvActiveGoals.setText(String.valueOf(json.optInt("active_goals", 0)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        if (progressListener != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(progressListener);
        }
        super.onDestroy();
    }
}
