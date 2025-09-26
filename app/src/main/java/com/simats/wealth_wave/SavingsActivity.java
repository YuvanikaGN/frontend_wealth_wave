package com.simats.wealth_wave;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.simats.wealth_wave.models.AddSavingRequestWithPlan;
import com.simats.wealth_wave.models.DeleteSavingRequestWithPlan;
import com.simats.wealth_wave.models.SavingItem;
import com.simats.wealth_wave.models.SavingsAdapter;
import com.simats.wealth_wave.responses.AddSavingResponseWithPlan;
import com.simats.wealth_wave.responses.DeleteSavingResponseWithPlan;
import com.simats.wealth_wave.responses.GetSavingsResponse;
import com.simats.wealth_wave.retrofit.RetrofitClient;
import com.simats.wealth_wave.ui.LoginPageActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavingsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "WealthWavePrefs";
    private static final String USER_PREFS_NAME = "UserPrefs";

    private TextView tvIncome, tvIncome2, tvRemaining, tvSavedAmount, progressPercentageText,viewAll;
    private EditText etSavedAmount;
    private RecyclerView rvSavingsHistory;
    private androidx.appcompat.widget.AppCompatButton addSavedBtn;
    private CircularProgressIndicator circularIndicator;

    private int selectedPlanId;
    private double income, target, totalSaved;
    private List<SavingItem> savingsList = new ArrayList<>();
    private SavingsAdapter adapter;
    private ImageView menuIcon;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private LinearLayout nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings);


        tvIncome = findViewById(R.id.tvIncome);
        tvIncome2 = findViewById(R.id.tvIncome2);
        tvRemaining = findViewById(R.id.tvRemaining);
        tvSavedAmount = findViewById(R.id.tvSavedAmount);
        progressPercentageText = findViewById(R.id.progressPercentageText);
        etSavedAmount = findViewById(R.id.etSavedAmount);
        addSavedBtn = findViewById(R.id.addSavedBtn);
        rvSavingsHistory = findViewById(R.id.rvSavingsHistory);
        circularIndicator = findViewById(R.id.circularIndicator);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);

        // Display logged-in user's name/email
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "User Name");
        String email = prefs.getString("email", "user@gmail.com");


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
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        navUserName.setText(name);
        navUserEmail.setText(email);

        TextView viewAll = findViewById(R.id.viewAll);
        viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(SavingsActivity.this, SavingsHistoryActivity.class);
            intent.putExtra("planId", selectedPlanId); // pass current planId
            startActivity(intent);
        });

        LinearLayout nav_view = headerView.findViewById(R.id.nav_view);
        nav_view.setOnClickListener(v -> {
            startActivity(new Intent(SavingsActivity.this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
        });


        // Drawer toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_savings_plan_bot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home_bot)
                startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0); // remove transition

            if (id == R.id.nav_savings_plan_bot) {
                overridePendingTransition(0, 0); // remove transition
            } else if (id == R.id.nav_investment_bot) {
                startActivity(new Intent(this, ProgressTrackingActivity.class));
                overridePendingTransition(0, 0);
            } else if (id == R.id.nav_profile_bot) {
                startActivity(new Intent(this, UserProfileActivity.class));
                overridePendingTransition(0, 0);
            }
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

            if (id == R.id.nav_home) startActivity(new Intent(SavingsActivity.this, HomeActivity.class));
            else if (id == R.id.nav_savings_plan) startActivity(new Intent(SavingsActivity.this, SavingsActivity.class));
            else if (id == R.id.nav_investment) startActivity(new Intent(SavingsActivity.this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_transactions) startActivity(new Intent(SavingsActivity.this, TransactionsActivity.class));
            else if (id == R.id.nav_edit_savings) startActivity(new Intent(SavingsActivity.this, EditSavingsActivity.class));
            else if (id == R.id.nav_chatbot) startActivity(new Intent(SavingsActivity.this, ChatbotActivity.class));
            else if (id == R.id.nav_logout) {
                startActivity(new Intent(SavingsActivity.this, LoginPageActivity.class));
                finish();
            }

            return true;
        });

        rvSavingsHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavingsAdapter(savingsList);
        rvSavingsHistory.setAdapter(adapter);

        addSavedBtn.setOnClickListener(v -> addSavedAmount());



        loadPlanDetails();

        adapter.setOnItemLongClickListener((position, item) -> {
            new androidx.appcompat.app.AlertDialog.Builder(SavingsActivity.this)
                    .setTitle("Delete Saving")
                    .setMessage("Do you want to delete this saving?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteSaving(position, item))
                    .setNegativeButton("No", null)
                    .show();
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }



    private final BroadcastReceiver savingsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            circularIndicator.setVisibility(View.VISIBLE);
            progressPercentageText.setVisibility(View.VISIBLE);
            rvSavingsHistory.setVisibility(View.INVISIBLE);


            loadSavingsFromBackend(); // always fetch latest from server
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(savingsUpdateReceiver, new IntentFilter("com.simats.wealth_wave.UPDATE_SAVINGS"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(savingsUpdateReceiver);
    }


    private void loadSavingsHistory() {
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        savingsList.clear();

        int count = userPrefs.getInt("total_saved_count_" + selectedPlanId, 0);

        for (int i = 0; i < count; i++) {
            double amount = Double.longBitsToDouble(
                    userPrefs.getLong("saved_item_" + selectedPlanId + "_" + i, Double.doubleToLongBits(0))
            );
            String date = userPrefs.getString("saved_item_date_" + selectedPlanId + "_" + i, "");
            int id = userPrefs.getInt("saved_item_id_" + selectedPlanId + "_" + i, -1);

            SavingItem item = new SavingItem(id, amount, date);
            savingsList.add(item);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlanDetails();         // Load plan info
        loadSavingsFromBackend();  // Always fetch fresh savings from server
    }



    private void loadSavingsFromBackend() {
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", 0);

        if (userId == 0 || selectedPlanId == -1) return;

        RetrofitClient.getInstance().getApi()
                .getSavings(userId, selectedPlanId)
                .enqueue(new Callback<GetSavingsResponse>() {
                    @Override
                    public void onResponse(Call<GetSavingsResponse> call, Response<GetSavingsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                            GetSavingsResponse res = response.body();

                            // Update totalSaved
                            totalSaved = res.getTotalSavings();

                            // Update RecyclerView
                            savingsList.clear();
                            for (GetSavingsResponse.SavingData data : res.getData()) {
                                savingsList.add(new SavingItem(data.getId(), data.getAmount(), data.getDateTime()));
                            }
                            adapter.notifyDataSetChanged();

                            updateUI(false);

                            // Optionally, update SharedPreferences for offline access
                            SharedPreferences.Editor editor = userPrefs.edit();
                            editor.putLong("total_saved_" + selectedPlanId, Double.doubleToRawLongBits(totalSaved));
                            editor.putInt("total_saved_count_" + selectedPlanId, savingsList.size());
                            for (int i = 0; i < savingsList.size(); i++) {
                                SavingItem s = savingsList.get(i);
                                editor.putLong("saved_item_" + selectedPlanId + "_" + i, Double.doubleToRawLongBits(s.getAmount()));
                                editor.putString("saved_item_date_" + selectedPlanId + "_" + i, s.getDate());
                                editor.putInt("saved_item_id_" + selectedPlanId + "_" + i, s.getId());
                            }
                            editor.apply();

                        } else {
                            Toast.makeText(SavingsActivity.this, "No savings found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSavingsResponse> call, Throwable t) {
                        Toast.makeText(SavingsActivity.this, "Failed to fetch savings: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




    // --- Helper for safe double reading from SharedPreferences ---
    private double getSafeDouble(SharedPreferences prefs, String key) {
        try {
            return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(0)));
        } catch (ClassCastException e) {
            float oldValue = prefs.getFloat(key, 0f);
            prefs.edit().putLong(key, Double.doubleToRawLongBits(oldValue)).apply();
            return oldValue;
        }
    }

    private void loadPlanDetails() {
        SharedPreferences planPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        selectedPlanId = planPrefs.getInt("selected_plan_id", -1);
        if (selectedPlanId == -1) return;

        income = getSafeDouble(planPrefs, "selected_plan_income");
        target = getSafeDouble(planPrefs, "selected_plan_target");

        TextView goalPlan = findViewById(R.id.goalPlan);
        goalPlan.setText(planPrefs.getString("selected_plan_goal", "Your Goal"));

        updateUI(false); // just update plan UI, not RecyclerView
    }



    private void addSavedAmount() {
        String input = etSavedAmount.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        int userIdInt = userPrefs.getInt("user_id", 0);
        if (userIdInt == 0) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = String.valueOf(userIdInt);

        addSavedBtn.setEnabled(false);
        String dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        AddSavingRequestWithPlan request = new AddSavingRequestWithPlan(
                userId,
                String.valueOf(amount),
                String.valueOf(selectedPlanId)
        );

        RetrofitClient.getInstance().getApi().addSaving(request)
                .enqueue(new retrofit2.Callback<AddSavingResponseWithPlan>() {
                    @Override
                    public void onResponse(Call<AddSavingResponseWithPlan> call, retrofit2.Response<AddSavingResponseWithPlan> response) {
                        addSavedBtn.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                            int savingId = response.body().getSavingId();

                            // Update local totalSaved & SharedPreferences
                            totalSaved += amount;
                            savingsList.add(new SavingItem(savingId, amount, dateTime));
                            adapter.notifyItemInserted(savingsList.size() - 1);
                            rvSavingsHistory.scrollToPosition(savingsList.size() - 1);

                            int historySize = userPrefs.getInt("total_saved_count_" + selectedPlanId, 0);
                            SharedPreferences.Editor editor = userPrefs.edit();
                            editor.putLong("total_saved_" + selectedPlanId, Double.doubleToRawLongBits(totalSaved));
                            editor.putLong("saved_item_" + selectedPlanId + "_" + historySize, Double.doubleToRawLongBits(amount));
                            editor.putString("saved_item_date_" + selectedPlanId + "_" + historySize, dateTime);
                            editor.putInt("saved_item_id_" + selectedPlanId + "_" + historySize, savingId);
                            editor.putInt("total_saved_count_" + selectedPlanId, historySize + 1);
                            editor.apply();

                            // 🔥 Save last save time for reminders
                            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            prefs.edit().putLong("last_save_time", System.currentTimeMillis()).apply();

                            etSavedAmount.setText("");
                            updateUI(true); // animate after adding
                        }

                        else {
                            Toast.makeText(SavingsActivity.this, "Server error: " +
                                    (response.body() != null ? response.body().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AddSavingResponseWithPlan> call, Throwable t) {
                        addSavedBtn.setEnabled(true);
                        Toast.makeText(SavingsActivity.this, "Failed to save: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteSaving(int position, SavingItem item) {
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);

        totalSaved -= item.getAmount();
        if (totalSaved < 0) totalSaved = 0;

        int historySize = userPrefs.getInt("total_saved_count_" + selectedPlanId, 0);
        SharedPreferences.Editor editor = userPrefs.edit();

        for (int i = position; i < historySize - 1; i++) {
            double nextAmount = Double.longBitsToDouble(userPrefs.getLong("saved_item_" + selectedPlanId + "_" + (i + 1), Double.doubleToLongBits(0)));
            String nextDate = userPrefs.getString("saved_item_date_" + selectedPlanId + "_" + (i + 1), "");
            int nextId = userPrefs.getInt("saved_item_id_" + selectedPlanId + "_" + (i + 1), 0);

            editor.putLong("saved_item_" + selectedPlanId + "_" + i, Double.doubleToRawLongBits(nextAmount));
            editor.putString("saved_item_date_" + selectedPlanId + "_" + i, nextDate);
            editor.putInt("saved_item_id_" + selectedPlanId + "_" + i, nextId);
        }

        editor.remove("saved_item_" + selectedPlanId + "_" + (historySize - 1));
        editor.remove("saved_item_date_" + selectedPlanId + "_" + (historySize - 1));
        editor.remove("saved_item_id_" + selectedPlanId + "_" + (historySize - 1));
        editor.putInt("total_saved_count_" + selectedPlanId, historySize - 1);
        editor.putLong("total_saved_" + selectedPlanId, Double.doubleToRawLongBits(totalSaved));
        editor.apply();

        savingsList.remove(position);
        adapter.notifyItemRemoved(position);


        updateUI(true); // animate after adding

        RetrofitClient.getInstance().getApi()
                .deleteSaving(new DeleteSavingRequestWithPlan(item.getId(), selectedPlanId))
                .enqueue(new retrofit2.Callback<DeleteSavingResponseWithPlan>() {
                    @Override
                    public void onResponse(Call<DeleteSavingResponseWithPlan> call, retrofit2.Response<DeleteSavingResponseWithPlan> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                            Toast.makeText(SavingsActivity.this, "Saving deleted ✅", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SavingsActivity.this, "Server deletion failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteSavingResponseWithPlan> call, Throwable t) {
                        Toast.makeText(SavingsActivity.this, "Failed to delete: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(boolean animate) {
        // Ensure the circular indicator and text are visible
        circularIndicator.setVisibility(android.view.View.VISIBLE);
        progressPercentageText.setVisibility(android.view.View.VISIBLE);

        // Sum all savings from history
        double totalSavedFromHistory = 0;
        for (SavingItem item : savingsList) {
            totalSavedFromHistory += item.getAmount();
        }
        totalSaved = totalSavedFromHistory; // update the totalSaved variable

        // Update TextViews
        tvIncome.setText("₹ " + String.format("%.2f", income));
        tvIncome2.setText("₹ " + String.format("%.2f", target));
        tvSavedAmount.setText("Saved - ₹ " + String.format("%.2f", totalSaved));

        // Calculate percentage
        int percentage = target > 0 ? (int) ((totalSaved / target) * 100) : 0;
        if (percentage > 100) percentage = 100;

        // Update tvRemaining based on progress
        // Change tvRemaining text and color
        if (percentage >= 100) {
            tvRemaining.setText("Target Reached ✅");
            tvRemaining.setTextColor(ContextCompat.getColor(this, R.color.green)); // green text
        } else {
            tvRemaining.setText("₹ " + String.format("%.2f", Math.max(target - totalSaved, 0)) + " Pending");
            tvRemaining.setTextColor(ContextCompat.getColor(this, R.color.red)); // default red
        }


        // Update progress text and progress bar
        progressPercentageText.setText(percentage + "%");
        circularIndicator.setVisibility(android.view.View.VISIBLE);

        if (animate) {
            int currentProgress = circularIndicator.getProgress();
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(circularIndicator, "progress", currentProgress, percentage);
            progressAnimator.setDuration(500);
            progressAnimator.start();
        } else {
            circularIndicator.setProgress(percentage);
        }
    }

}