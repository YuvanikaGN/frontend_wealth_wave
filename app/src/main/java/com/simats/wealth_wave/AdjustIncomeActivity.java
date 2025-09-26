package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.simats.wealth_wave.responses.IncomeResponse;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdjustIncomeActivity extends AppCompatActivity {

    private ImageView backArrow, menuIcon;
    private AppCompatButton cancelBtn, saveButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private EditText newIncomeInput;
    private CircularProgressIndicator circularIndicator;
    private TextView percentageText;

    private static final String PREF_NAME = "WealthWavePrefs";
    private static final String KEY_APPROX_MONEY = "approx_money";
    private static final String KEY_SAVED_PERCENTAGE = "saved_percentage";

    private double monthlyIncome = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.adjust_income);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backArrow = findViewById(R.id.backArrow);
        menuIcon = findViewById(R.id.menuIcon);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveButton = findViewById(R.id.saveButton);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        newIncomeInput = findViewById(R.id.newIncomeInput);
        circularIndicator = findViewById(R.id.circularIndicator);
        percentageText = findViewById(R.id.percentageText);

        // Fetch monthly income from API
//        fetchMonthlyIncome();

        backArrow.setOnClickListener(v -> goToSavings());

        cancelBtn.setOnClickListener(v -> goToSavings());

        saveButton.setOnClickListener(v -> saveAndReturn());

        // Navigation drawer menu icon
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        // Navigation drawer items
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(this, ProgressTrackingActivity.class));
            }
//            else if (id == R.id.nav_income) {
//                startActivity(new Intent(this, IncomeActivity.class));
//            }
            else if (id == R.id.nav_transactions) {
                startActivity(new Intent(this, TransactionsActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(this, LogoPageActivity.class));
                finish();
            }
            overridePendingTransition(0, 0);
            return true;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // highlight "Home" when we are in Home screen
        bottomNavigationView.setSelectedItemId(R.id.nav_home_bot);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home_bot) {
                return true; // already here
            } else if (id == R.id.nav_savings_plan_bot) {
                startActivity(new Intent(this, SavingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_investment_bot) {
                startActivity(new Intent(this, ProgressTrackingActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile_bot) {
                startActivity(new Intent(this, UserProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });


        newIncomeInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String inputStr = s.toString().trim();
                if (inputStr.isEmpty()) {
                    circularIndicator.setProgress(0);
                    percentageText.setText("0%");
                    return;
                }

                try {
                    double enteredAmount = Double.parseDouble(inputStr);
                    if (enteredAmount > monthlyIncome) {
                        Toast.makeText(AdjustIncomeActivity.this, "Entered amount exceeds monthly income", Toast.LENGTH_SHORT).show();
                        circularIndicator.setProgress(0);
                        percentageText.setText("0%");
                        return;
                    }

                    int percent = (int) ((enteredAmount / monthlyIncome) * 100);
                    circularIndicator.setProgress(percent);
                    percentageText.setText(percent + "%");

                } catch (NumberFormatException e) {
                    circularIndicator.setProgress(0);
                    percentageText.setText("0%");
                }
            }
        });

        // Load saved approx_money and percentage from prefs
        loadSavedValues();

        // Status bar color
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }

//    private void fetchMonthlyIncome() {
//        ApiService apiService = RetrofitClient.getInstance().getApi();
//        apiService.getIncome().enqueue(new Callback<IncomeResponse>() {
//            @Override
//            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String incomeStr = response.body().getIncome();
//                    try {
//                        monthlyIncome = Double.parseDouble(incomeStr);
//                    } catch (NumberFormatException e) {
//                        monthlyIncome = 0;
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<IncomeResponse> call, Throwable t) {
//                Toast.makeText(AdjustIncomeActivity.this, "Failed to fetch income", Toast.LENGTH_SHORT).show();
//                monthlyIncome = 0;
//            }
//        });
//    }

    private void loadSavedValues() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        float savedIncome = prefs.getFloat(KEY_APPROX_MONEY, 0);
        int savedPercent = prefs.getInt(KEY_SAVED_PERCENTAGE, 0);

        if (savedIncome > 0) {
            newIncomeInput.setText(String.format("%.2f", savedIncome));
        }

        if (savedPercent > 0) {
            circularIndicator.setProgress(savedPercent);
            percentageText.setText(savedPercent + "%");
        }
    }

    private void saveAndReturn() {
        String inputStr = newIncomeInput.getText().toString().trim();
        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double enteredAmount = Double.parseDouble(inputStr);
            if (enteredAmount > monthlyIncome) {
                Toast.makeText(this, "Entered amount cannot exceed monthly income", Toast.LENGTH_SHORT).show();
                return;
            }

            int percent = circularIndicator.getProgress();

            // Save to SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            prefs.edit()
                    .putFloat(KEY_APPROX_MONEY, (float) enteredAmount)
                    .putInt(KEY_SAVED_PERCENTAGE, percent)
                    .apply();

            // Return result to SavingsActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_APPROX_MONEY, (float) enteredAmount);
            resultIntent.putExtra(KEY_SAVED_PERCENTAGE, percent);
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToSavings() {
        Intent intent = new Intent(this, SavingsActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}
