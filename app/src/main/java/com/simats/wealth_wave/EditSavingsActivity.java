package com.simats.wealth_wave;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.simats.wealth_wave.models.AddPlanRequest;
import com.simats.wealth_wave.models.DeletePlanRequest;
import com.simats.wealth_wave.models.UpdateSavingsPlanRequest;
import com.simats.wealth_wave.responses.AddPlanResponse;
import com.simats.wealth_wave.responses.BaseResponse;
import com.simats.wealth_wave.responses.GetAllPlansResponse;
import com.simats.wealth_wave.responses.GetSavingsPlanResponse;
import com.simats.wealth_wave.responses.UpdateSavingsPlanResponse;
import com.simats.wealth_wave.retrofit.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSavingsActivity extends AppCompatActivity {

    private EditText etGoal, etTargetAmount, etIncome, etDuration;
    private ImageView menuIcon, backArrow;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private int userId;

    private static final String USER_PREFS_NAME = "UserPrefs";
    private GetAllPlansResponse.Data selectedPlan = null;
    private AppCompatButton saveChangesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_savings);

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get logged-in userId
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Nav header values
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        navUserName.setText(prefs.getString("name", "User Name"));
        navUserEmail.setText(prefs.getString("email", "user@gmail.com"));

        // Bind views
        etGoal = findViewById(R.id.etGoal);
        etTargetAmount = findViewById(R.id.etTargetAmount);
        etIncome = findViewById(R.id.etIncome);
        etDuration = findViewById(R.id.etDuration);

        saveChangesButton = findViewById(R.id.saveChangesButton);

        saveChangesButton.setOnClickListener(v -> {
            if (selectedPlan == null) {
                Toast.makeText(this, "No plan selected", Toast.LENGTH_SHORT).show();
                return;
            }

            String goal = etGoal.getText().toString().trim();
            String targetStr = etTargetAmount.getText().toString().trim();
            String incomeStr = etIncome.getText().toString().trim();
            String durationDate = etDuration.getText().toString().trim();

            if (goal.isEmpty() || targetStr.isEmpty() || incomeStr.isEmpty() || durationDate.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double targetAmount, income;
            try {
                targetAmount = Double.parseDouble(targetStr);
                income = Double.parseDouble(incomeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create update request
            UpdateSavingsPlanRequest request = new UpdateSavingsPlanRequest(
                    selectedPlan.getId(), // update this specific plan
                    goal,
                    targetAmount,
                    income,
                    durationDate
            );

            saveChangesButton.setEnabled(false);

            RetrofitClient.getInstance().getApi().updateSavingsPlan(request)
                    .enqueue(new Callback<UpdateSavingsPlanResponse>() {
                        @Override
                        public void onResponse(Call<UpdateSavingsPlanResponse> call, Response<UpdateSavingsPlanResponse> response) {
                            saveChangesButton.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(EditSavingsActivity.this,
                                        response.body().getMessage() + "\nMonthly saving: " + response.body().getApproxMoney(),
                                        Toast.LENGTH_LONG).show();
                                selectedPlan = null;
                                saveChangesButton.setVisibility(View.GONE);
                                etGoal.setText("");
                                etTargetAmount.setText("");
                                etIncome.setText("");
                                etDuration.setText("");
                            } else {
                                Toast.makeText(EditSavingsActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateSavingsPlanResponse> call, Throwable t) {
                            saveChangesButton.setEnabled(true);
                            Toast.makeText(EditSavingsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        AppCompatButton updatePlanButton = findViewById(R.id.updatePlanButton);

        updatePlanButton.setOnClickListener(v -> {
            RetrofitClient.getInstance().getApi().getAllPlans(userId)
                    .enqueue(new Callback<GetAllPlansResponse>() {
                        @Override
                        public void onResponse(Call<GetAllPlansResponse> call, Response<GetAllPlansResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                                List<GetAllPlansResponse.Data> plans = response.body().getData();
                                if (plans.isEmpty()) {
                                    Toast.makeText(EditSavingsActivity.this, "No plans found", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String[] goalArray = new String[plans.size()];
                                for (int i = 0; i < plans.size(); i++) {
                                    goalArray[i] = plans.get(i).getGoal();
                                }

                                new androidx.appcompat.app.AlertDialog.Builder(EditSavingsActivity.this)
                                        .setTitle("Select a Plan")
                                        .setItems(goalArray, (dialog, which) -> {
                                            // Store selected plan
                                            selectedPlan = plans.get(which);

                                            // Fill fields
                                            etGoal.setText(selectedPlan.getGoal());
                                            etTargetAmount.setText(String.valueOf(selectedPlan.getTarget_amount()));
                                            etIncome.setText(String.valueOf(selectedPlan.getIncome()));
                                            etDuration.setText(selectedPlan.getDuration());

                                            // Show save changes button
                                            saveChangesButton.setVisibility(View.VISIBLE);
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();

                            } else {
                                Toast.makeText(EditSavingsActivity.this, "Failed to fetch plans", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetAllPlansResponse> call, Throwable t) {
                            Toast.makeText(EditSavingsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        AppCompatButton addPlanButton = findViewById(R.id.addPlanButton);

        addPlanButton.setOnClickListener(v -> {
            String goal = etGoal.getText().toString().trim();
            String targetStr = etTargetAmount.getText().toString().trim();
            String incomeStr = etIncome.getText().toString().trim();
            String durationDate = etDuration.getText().toString().trim(); // yyyy-mm-dd from DatePicker

            if (goal.isEmpty() || targetStr.isEmpty() || incomeStr.isEmpty() || durationDate.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double targetAmount, income;
            try {
                targetAmount = Double.parseDouble(targetStr);
                income = Double.parseDouble(incomeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Always create a new plan with 0 saved progress
            AddPlanRequest request = new AddPlanRequest(userId, goal, targetAmount, income, durationDate);

            addPlanButton.setEnabled(false);

            RetrofitClient.getInstance().getApi().addSavingsPlan(request)
                    .enqueue(new Callback<AddPlanResponse>() {
                        @Override
                        public void onResponse(Call<AddPlanResponse> call, Response<AddPlanResponse> response) {
                            addPlanButton.setEnabled(true);

                            if (response.isSuccessful() && response.body() != null) {
                                AddPlanResponse res = response.body();
                                Toast.makeText(EditSavingsActivity.this,
                                        res.getMessage() + "\nMonthly saving: " + res.getApproxMoney(),
                                        Toast.LENGTH_LONG).show();

                                // ✅ Reset input fields
                                etGoal.setText("");
                                etTargetAmount.setText("");
                                etIncome.setText("");
                                etDuration.setText("");

                                // ✅ Reset progress bar when new plan is added
                                // Assuming you have ProgressBar reference somewhere
                                // progressBar.setProgress(0);

                                // ✅ Hide saveChanges button
                                saveChangesButton.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(EditSavingsActivity.this, "Add plan failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddPlanResponse> call, Throwable t) {
                            addPlanButton.setEnabled(true);
                            Toast.makeText(EditSavingsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });






        etDuration.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditSavingsActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String displayDate = String.format(Locale.getDefault(),
                                "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        etDuration.setText(displayDate); // store date string
                    }, year, month, day);
            datePickerDialog.show();
        });


        AppCompatButton deletePlanButton = findViewById(R.id.deletePlanButton);
        deletePlanButton.setOnClickListener(v -> {
            // Fetch all plans and display them
            fetchAndDisplayPlans();
        });


        menuIcon = findViewById(R.id.menuIcon);
        backArrow = findViewById(R.id.backArrow);
        drawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.navigationView);

        // Load user savings plan
//        loadUserData();

        // Status & nav bar colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));
        }

        // Drawer toggle
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

            if (id == R.id.nav_home) startActivity(new Intent(this, HomeActivity.class));
            else if (id == R.id.nav_savings_plan) startActivity(new Intent(this, SavingsActivity.class));
            else if (id == R.id.nav_investment) startActivity(new Intent(this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_transactions) startActivity(new Intent(this, TransactionsActivity.class));
            else if (id == R.id.nav_edit_savings) startActivity(new Intent(this, EditSavingsActivity.class));
            else if (id == R.id.nav_chatbot) startActivity(new Intent(this, ChatbotActivity.class));
            else if (id == R.id.nav_logout) {
                startActivity(new Intent(this, LogoPageActivity.class));
                finish();
            }

            overridePendingTransition(0, 0);
            return true;
        });

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home_bot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home_bot) startActivity(new Intent(this, HomeActivity.class));
            else if (id == R.id.nav_savings_plan_bot) startActivity(new Intent(this, SavingsActivity.class));
            else if (id == R.id.nav_investment_bot) startActivity(new Intent(this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_profile_bot) startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        });

        backArrow.setOnClickListener(v -> finish());
    }



    private void fetchAndDisplayPlans() {
        RetrofitClient.getInstance().getApi().getAllPlans(userId)
                .enqueue(new Callback<GetAllPlansResponse>() {
                    @Override
                    public void onResponse(Call<GetAllPlansResponse> call, Response<GetAllPlansResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                            List<GetAllPlansResponse.Data> plans = response.body().getData();

                            if (plans.isEmpty()) {
                                Toast.makeText(EditSavingsActivity.this, "No plans found", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Create an array of goal texts
                            String[] goalArray = new String[plans.size()];
                            for (int i = 0; i < plans.size(); i++) {
                                goalArray[i] = plans.get(i).getGoal();
                            }

                            // Show clickable list
                            new androidx.appcompat.app.AlertDialog.Builder(EditSavingsActivity.this)
                                    .setTitle("Your Plans")
                                    .setItems(goalArray, (dialog, which) -> {
                                        // Selected plan
                                        GetAllPlansResponse.Data selectedPlan = plans.get(which);

                                        // Show delete confirmation
                                        new androidx.appcompat.app.AlertDialog.Builder(EditSavingsActivity.this)
                                                .setTitle("Delete Plan")
                                                .setMessage("Are you sure you want to delete this goal?\n\n" + selectedPlan.getGoal())
                                                .setPositiveButton("Yes", (d, w) -> {
                                                    deletePlan(selectedPlan.getId());
                                                })
                                                .setNegativeButton("No", null)
                                                .show();
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();

                        } else {
                            Toast.makeText(EditSavingsActivity.this, "Failed to fetch plans", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAllPlansResponse> call, Throwable t) {
                        Toast.makeText(EditSavingsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletePlan(int planId) {
        DeletePlanRequest request = new DeletePlanRequest(planId);

        RetrofitClient.getInstance().getApi().deletePlan(request)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                            Toast.makeText(EditSavingsActivity.this, "Plan deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditSavingsActivity.this, "Failed to delete plan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(EditSavingsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadUserData() {
        RetrofitClient.getInstance().getApi().getSavingsPlan(userId)
                .enqueue(new Callback<GetSavingsPlanResponse>() {
                    @Override
                    public void onResponse(Call<GetSavingsPlanResponse> call, Response<GetSavingsPlanResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                            GetSavingsPlanResponse.Data data = (GetSavingsPlanResponse.Data) response.body().getData();

                            etGoal.setText(data.getGoal());
                            etTargetAmount.setText(String.valueOf(data.getTarget_amount()));
                            etIncome.setText(String.valueOf(data.getIncome()));
                            etDuration.setText(data.getDuration());


                        } else {
                            Toast.makeText(EditSavingsActivity.this, "No plan found for this user", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSavingsPlanResponse> call, Throwable t) {
                        Toast.makeText(EditSavingsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}