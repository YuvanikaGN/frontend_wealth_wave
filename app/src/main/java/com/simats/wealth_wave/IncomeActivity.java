package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.simats.wealth_wave.models.UpdateIncomeRequest;
import com.simats.wealth_wave.responses.GenericResponse;
import com.simats.wealth_wave.responses.IncomeResponse;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.retrofit.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomeActivity extends AppCompatActivity {

    private ImageButton homeBtn, savingsBtn, investmentTrackingBtn, userprofileBtn;
    private ImageView menuIcon;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private TextView tvIncome;
    private AppCompatButton updateIncomeButton;
    private static final String USER_PREFS_NAME = "UserPrefs"; // same as login saves to

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.income_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // at the top of onCreate after setContentView(...)
        AppCompatButton addBtn = findViewById(R.id.addBtn);
        LinearLayout additionalIncome = findViewById(R.id.additionalIncome);
        EditText amountInput = findViewById(R.id.amount_input);
        AppCompatButton addIncomeBtn = findViewById(R.id.addIncome);

// Toggle dropdown
        addBtn.setOnClickListener(v -> {
            additionalIncome.setVisibility(
                    additionalIncome.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
        });

// Handle "Add income" click
        addIncomeBtn.setOnClickListener(v -> {
            String extraStr = amountInput.getText().toString().trim();
            if (extraStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double extra;
            try {
                extra = Double.parseDouble(extraStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (extra <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) Get current base income (prefer cached prefs; fallback to tvIncome)
            String baseStr = getIncomeFromPreferences();
            if (baseStr == null) {
                baseStr = stripCurrency(tvIncome.getText().toString());
            }
            double base = safeParseDouble(baseStr);

            // 2) Compute new total
            double newTotal = base + extra;

            // 3) Update backend -> on success update UI + cache
            updateIncomeApi(String.valueOf(newTotal));
        });


        tvIncome = findViewById(R.id.tvIncome);
        updateIncomeButton = findViewById(R.id.updateIncomeButton);

        updateIncomeButton.setOnClickListener(v -> showUpdateIncomeDialog());

//        addBtn = findViewById(R.id.addBtn);
//        LinearLayout additionalIncome = findViewById(R.id.additionalIncome);

        addBtn.setOnClickListener(v -> {
            if (additionalIncome.getVisibility() == View.GONE) {
                additionalIncome.setVisibility(View.VISIBLE);
            } else {
                additionalIncome.setVisibility(View.GONE);
            }
        });


        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) {
                startActivity(new Intent(IncomeActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(IncomeActivity.this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(IncomeActivity.this, ProgressTrackingActivity.class));
            }
//            else if (id == R.id.nav_income) {
//                // already here
//            }
            else if (id == R.id.nav_transactions) {
                startActivity(new Intent(IncomeActivity.this, TransactionsActivity.class));
            } else if (id == R.id.nav_edit_savings) {
                startActivity(new Intent(IncomeActivity.this, IncomeActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(IncomeActivity.this, LogoPageActivity.class));
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
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
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


        // ✅ Only fetch income from the correct API
        fetchIncomeFromApi();
    }

    private void showUpdateIncomeDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Update Monthly Income");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter new income");

        int paddingDp = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(paddingDp, paddingDp / 2, paddingDp, paddingDp / 2);

        input.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.blue_700)));
        input.setHintTextColor(getResources().getColor(R.color.blue_200));
        input.setTextColor(getResources().getColor(R.color.blue_900));

        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newIncomeStr = input.getText().toString().trim();
            if (!newIncomeStr.isEmpty()) {
                updateIncomeApi(newIncomeStr);
            } else {
                Toast.makeText(this, "Please enter a valid income", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(R.color.blue_700));
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.blue_700));
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void updateIncomeApi(String newIncome) {
        int userId = getUserIdFromPreferences();
        double incomeDouble = safeParseDouble(newIncome);

        ApiService apiService = RetrofitClient.getInstance().getApi();
        UpdateIncomeRequest request = new UpdateIncomeRequest(userId, incomeDouble);

        Call<GenericResponse> call = apiService.updateIncome(request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Update UI
                    tvIncome.setText("₹ " + formatMoney(incomeDouble));

                    // ✅ Cache for other screens
                    saveIncomeToPreferences(String.valueOf(incomeDouble));

                    // ✅ Optional: Reset the dropdown UI
                    EditText amountInput = findViewById(R.id.amount_input);
                    amountInput.setText("");
                    LinearLayout additionalIncome = findViewById(R.id.additionalIncome);
                    additionalIncome.setVisibility(View.GONE);

                    Toast.makeText(IncomeActivity.this, "Income updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(IncomeActivity.this, "Failed to update income", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(IncomeActivity.this, "Error updating income: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double safeParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0d;
        }
    }

    private String stripCurrency(String s) {
        // Remove everything except digits and dot
        return s == null ? "" : s.replaceAll("[^\\d.]", "");
    }

    private String formatMoney(double amount) {
        // Simple 2-decimal formatting; replace with Indian formatting if you prefer
        return String.format(java.util.Locale.US, "%,.2f", amount);
    }


    private void saveIncomeToPreferences(String income) {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString("income", income).apply();
    }

    private String getIncomeFromPreferences() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("income", null);
    }

    private void fetchIncomeFromApi() {
        String cachedIncome = getIncomeFromPreferences();
        if (cachedIncome != null) {
            tvIncome.setText("₹ " + cachedIncome);
        }

        int userId = getUserIdFromPreferences();
        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<IncomeResponse> call = apiService.getIncome(userId);

        call.enqueue(new Callback<IncomeResponse>() {
            @Override
            public void onResponse(Call<IncomeResponse> call, Response<IncomeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String income = response.body().getIncome();
                    tvIncome.setText("₹ " + income);
                    saveIncomeToPreferences(income);
                } else {
                    Toast.makeText(IncomeActivity.this, "Failed to load income", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<IncomeResponse> call, Throwable t) {
                Toast.makeText(IncomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
