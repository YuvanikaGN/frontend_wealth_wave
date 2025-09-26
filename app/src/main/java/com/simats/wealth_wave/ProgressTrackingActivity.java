package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.simats.wealth_wave.models.Transaction;
import com.simats.wealth_wave.responses.GetSavingsResponse;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.ui.LoginPageActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


public class ProgressTrackingActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private ImageView backArrow, menuIcon;
    private TextView tvTips;

    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private TextView tvIncome, tvExpense, tvBalance;

    private ApiService apiService;
    private int userId;
    private static final String USER_PREFS_NAME = "UserPrefs"; // match LoginPageActivity
    private LineChart lineChartSavings;
    private static final String PREF_NAME = "WealthWavePrefs";   // same as SavingsHistoryActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_tracking);


        // ✅ get SharedPreferences only after context is ready
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = userPrefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPageActivity.class));
            finish();
            return;
        }

        pieChart = findViewById(R.id.pieChart);
        lineChartSavings = findViewById(R.id.lineChartSavings);
        setupSavingsLineChart();  // styling once
        tabLayout = findViewById(R.id.tabLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        backArrow = findViewById(R.id.backArrow);

        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvBalance = findViewById(R.id.tvBalance);

        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(ProgressTrackingActivity.this, HomeActivity.class));
            overridePendingTransition(0, 0);
        });

        menuIcon = findViewById(R.id.menuIcon);

        tvTips = findViewById(R.id.tvTips);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigationView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // highlight "Home" when we are in Home screen
        bottomNavigationView.setSelectedItemId(R.id.nav_investment_bot);
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

        // Drawer open/close
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });


        // Navigation menu clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) {
                startActivity(new Intent(ProgressTrackingActivity.this, ProgressTrackingActivity.class));
            } else if (id == R.id.nav_savings_plan) {
                startActivity(new Intent(ProgressTrackingActivity.this, SavingsActivity.class));
            } else if (id == R.id.nav_investment) {
                startActivity(new Intent(ProgressTrackingActivity.this, ProgressTrackingActivity.class));
            }
            else if (id == R.id.nav_chatbot) {
                startActivity(new Intent(ProgressTrackingActivity.this, ChatbotActivity.class));
            }
            else if (id == R.id.nav_transactions) {
                startActivity(new Intent(ProgressTrackingActivity.this, TransactionsActivity.class));
            }else if (id == R.id.nav_edit_savings) {
                startActivity(new Intent(ProgressTrackingActivity.this, EditSavingsActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(ProgressTrackingActivity.this, LogoPageActivity.class));
                finish();
            }
            overridePendingTransition(0, 0);
            return true;
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);

// Get references
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);

        LinearLayout nav_view = headerView.findViewById(R.id.nav_view);
        nav_view.setOnClickListener(v -> {
            startActivity(new Intent(ProgressTrackingActivity.this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
        });

// Fetch from SharedPreferences (saved earlier after login/profile fetch)
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "User Name");
        String email = prefs.getString("email", "user@gmail.com");

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

// Set them
        navUserName.setText(name);
        navUserEmail.setText(email);


        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.24.93.232/app_database/") // use your local IP for real device
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Tabs
        tabLayout.addTab(tabLayout.newTab().setText("Income"));
        tabLayout.addTab(tabLayout.newTab().setText("Expense"));

        // Load default chart
        loadIncomeChart(userId);

        // Tab toggle listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadIncomeChart(userId);
                } else {
                    loadExpenseChart(userId);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));


        loadTransactions();
        setupSavingsLineChart(); // just styling
        loadSavingsLineDataFromBackend(); // fetch and display savings from backend


    }

    private void loadSavingsLineDataFromBackend() {
        SharedPreferences planPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int selectedPlanId = planPrefs.getInt("selected_plan_id", -1);
        if (selectedPlanId == -1) {
            lineChartSavings.clear();
            lineChartSavings.setNoDataText("No plan selected");
            lineChartSavings.invalidate();
            return;
        }

        apiService.getSavings(userId, selectedPlanId).enqueue(new Callback<GetSavingsResponse>() {
            @Override
            public void onResponse(Call<GetSavingsResponse> call, Response<GetSavingsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                    List<GetSavingsResponse.SavingData> savings = response.body().getData();

                    if (savings.isEmpty()) {
                        lineChartSavings.clear();
                        lineChartSavings.setNoDataText("No savings recorded yet");
                        lineChartSavings.invalidate();
                        return;
                    }

                    ArrayList<Entry> entries = new ArrayList<>();
                    ArrayList<String> xLabels = new ArrayList<>();

                    SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputSdf = new SimpleDateFormat("MMM dd", Locale.getDefault());

                    double cumulative = 0;
                    for (int i = 0; i < savings.size(); i++) {
                        GetSavingsResponse.SavingData item = savings.get(i);
                        cumulative += item.getAmount();
                        entries.add(new Entry(i, (float) cumulative));

                        try {
                            Date date = inputSdf.parse(item.getDateTime());
                            xLabels.add(date != null ? outputSdf.format(date) : "?");
                        } catch (ParseException e) {
                            xLabels.add("?");
                        }
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Cumulative Savings");
                    dataSet.setLineWidth(2f);
                    dataSet.setCircleRadius(4f);
                    dataSet.setDrawValues(false);
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSet.setDrawFilled(true);
                    dataSet.setFillAlpha(50);
                    dataSet.setColor(0xFF0B4CBA);
                    dataSet.setCircleColor(0xFF0B4CBA);
                    dataSet.setFillColor(0xFF0B4CBA);

                    LineData lineData = new LineData(dataSet);
                    lineChartSavings.setData(lineData);

                    XAxis xAxis = lineChartSavings.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
                    xAxis.setLabelCount(Math.min(5, xLabels.size()), true);
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setAvoidFirstLastClipping(true);

                    lineChartSavings.getAxisRight().setEnabled(false);
                    lineChartSavings.getDescription().setEnabled(false);
                    lineChartSavings.animateX(800);
                    lineChartSavings.invalidate();

                } else {
                    lineChartSavings.clear();
                    lineChartSavings.setNoDataText("Failed to fetch savings");
                    lineChartSavings.invalidate();
                }
            }

            @Override
            public void onFailure(Call<GetSavingsResponse> call, Throwable t) {
                lineChartSavings.clear();
                lineChartSavings.setNoDataText("Failed to fetch savings");
                lineChartSavings.invalidate();
                t.printStackTrace();
            }
        });
    }



    private void setupSavingsLineChart() {
        lineChartSavings.getDescription().setEnabled(false);
        lineChartSavings.setNoDataText("");
        lineChartSavings.setNoDataTextColor(Color.GRAY);
        lineChartSavings.setTouchEnabled(true);
        lineChartSavings.setPinchZoom(true);
        lineChartSavings.setScaleEnabled(true);

        XAxis xAxis = lineChartSavings.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // 1 step between labels
        xAxis.setLabelRotationAngle(0f);

        YAxis leftAxis = lineChartSavings.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);
        lineChartSavings.getAxisRight().setEnabled(false);
        lineChartSavings.getLegend().setEnabled(false);
    }

//    private void loadSavingsLineData() {
//        SharedPreferences planPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
//
//        int selectedPlanId = planPrefs.getInt("selected_plan_id", -1);
//        if (selectedPlanId == -1) {
//            lineChartSavings.clear();
//            lineChartSavings.setNoDataText("No plan selected");
//            lineChartSavings.invalidate();
//            return;
//        }
//
//        int historySize = userPrefs.getInt("total_saved_count_" + selectedPlanId, 0);
//        if (historySize == 0) {
//            lineChartSavings.clear();
//            lineChartSavings.invalidate();
//            return;
//        }
//
//        ArrayList<Entry> entries = new ArrayList<>();
//        ArrayList<String> xLabels = new ArrayList<>();
//
//        SimpleDateFormat inputSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        SimpleDateFormat outputSdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
//
//        double cumulative = 0;
//
//        for (int i = 0; i < historySize; i++) {
//            double amount = Double.longBitsToDouble(
//                    userPrefs.getLong("saved_item_" + selectedPlanId + "_" + i, Double.doubleToLongBits(0))
//            );
//            String dateStr = userPrefs.getString("saved_item_date_" + selectedPlanId + "_" + i, null);
//
//            cumulative += amount;
//            entries.add(new Entry(i, (float) cumulative));
//
//            // Format date for X-axis
//            if (dateStr != null && !dateStr.isEmpty()) {
//                try {
//                    Date date = inputSdf.parse(dateStr);
//                    xLabels.add(outputSdf.format(date)); // e.g., May 12
//                } catch (ParseException e) {
//                    xLabels.add("?"); // fallback for invalid date
//                }
//            } else {
//                xLabels.add("?"); // fallback for missing date
//            }
//        }
//
//        LineDataSet dataSet = new LineDataSet(entries, "Cumulative Savings");
//        dataSet.setLineWidth(2f);
//        dataSet.setCircleRadius(4f);
//        dataSet.setDrawValues(false);
//        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSet.setDrawFilled(true);
//        dataSet.setFillAlpha(50);
//        dataSet.setColor(0xFF0B4CBA);
//        dataSet.setCircleColor(0xFF0B4CBA);
//        dataSet.setFillColor(0xFF0B4CBA);
//
//        LineData lineData = new LineData(dataSet);
//        lineChartSavings.setData(lineData);
//
//        XAxis xAxis = lineChartSavings.getXAxis();
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
//        xAxis.setLabelCount(Math.min(5, xLabels.size()), true);
//        xAxis.setGranularity(1f);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setAvoidFirstLastClipping(true);
//
//        lineChartSavings.getAxisRight().setEnabled(false);
//        lineChartSavings.getDescription().setEnabled(false);
//        lineChartSavings.animateX(800);
//        lineChartSavings.invalidate();
//    }


    private void loadTransactions() {
        // Fetch incomes
        apiService.getTransactions(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    float totalIncome = 0f;
                    for (Transaction t : response.body()) {
                        totalIncome += t.getAmount();
                    }
                    tvIncome.setText("₹ " + totalIncome);
                    calculateBalance(totalIncome, -1); // -1 means expense not yet fetched
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(ProgressTrackingActivity.this, "Failed to load income", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch expenses
        apiService.getExpense(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    float totalExpense = 0f;
                    for (Transaction t : response.body()) {
                        totalExpense += t.getAmount();
                    }
                    tvExpense.setText("₹ " + totalExpense);
                    calculateBalance(-1, totalExpense); // -1 means income not yet fetched
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(ProgressTrackingActivity.this, "Failed to load expense", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadIncomeChart(int userId) {
        apiService.getTransactions(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Use a Map to sum amounts by category
                    Map<String, Float> categoryMap = new HashMap<>();
                    String[] categories = {"salary", "freelance", "gifts", "others"};
                    for (String cat : categories) categoryMap.put(cat, 0f);

                    for (Transaction t : response.body()) {
                        String cat = t.getCategory().toLowerCase();
                        if (categoryMap.containsKey(cat)) {
                            categoryMap.put(cat, categoryMap.get(cat) + t.getAmount());
                        } else {
                            categoryMap.put("others", categoryMap.get("others") + t.getAmount());
                        }
                    }

                    ArrayList<PieEntry> entries = new ArrayList<>();
                    ArrayList<Integer> colors = new ArrayList<>();

                    for (String cat : categories) {
                        float amount = categoryMap.get(cat);
                        if (amount > 0) { // Only show if non-zero
                            entries.add(new PieEntry(amount, capitalize(cat)));
                            colors.add(getCategoryColor(cat, "income"));
                        }
                    }

                    setPieChart(entries, colors);
                    updateSummaryAndTips(categoryMap, "income");
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                t.printStackTrace();
            }


        });
    }

    private void loadExpenseChart(int userId) {
        apiService.getExpense(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Float> categoryMap = new HashMap<>();
                    String[] categories = {"food", "transport", "entertainment", "shopping", "bills", "others"};
                    for (String cat : categories) categoryMap.put(cat, 0f);

                    for (Transaction t : response.body()) {
                        String cat = t.getCategory().toLowerCase();
                        if (categoryMap.containsKey(cat)) {
                            categoryMap.put(cat, categoryMap.get(cat) + t.getAmount());
                        } else {
                            categoryMap.put("others", categoryMap.get("others") + t.getAmount());
                        }
                    }

                    ArrayList<PieEntry> entries = new ArrayList<>();
                    ArrayList<Integer> colors = new ArrayList<>();

                    for (String cat : categories) {
                        float amount = categoryMap.get(cat);
                        if (amount > 0) {
                            entries.add(new PieEntry(amount, capitalize(cat)));
                            colors.add(getCategoryColor(cat, "expense"));
                        }
                    }

                    setPieChart(entries, colors);
                    updateSummaryAndTips(categoryMap, "expense");

                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String capitalize(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }


    private void setPieChart(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        // If there’s no data
        if (entries.isEmpty()) {
            pieChart.setCenterText("No transactions yet");
            pieChart.setCenterTextColor(Color.GRAY);
            pieChart.setCenterTextSize(14f);
            pieChart.setData(null); // remove old data
            pieChart.getLegend().setEnabled(false); // hide legend
        } else {
            pieChart.setCenterText(tabLayout.getSelectedTabPosition() == 0 ? "Income" : "Expense");
            pieChart.setCenterTextSize(16f);

            // Legend below chart
            Legend legend = pieChart.getLegend();
            legend.setEnabled(true);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setTextSize(12f);
            legend.setWordWrapEnabled(true);
        }

        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(800);
        pieChart.invalidate();
    }




    private int getCategoryColor(String category, String type) {
        if (type.equals("income")) {
            switch (category.toLowerCase()) {
                case "salary":
                    return 0xFFA5D6A7; // Light Green 💰
                case "freelance":
                    return 0xFF90CAF9; // Light Blue 🧑‍💻
                case "gifts":
                    return 0xFFF48FB1; // Light Pink 🎁
                case "others":
                    return 0xFFFFCC80; // Light Orange 📦
                default:
                    return 0xFFB0BEC5; // Light Gray (Fallback)
            }
        } else {
            switch (category.toLowerCase()) {
                case "food":
                    return 0xFFA5D6A7; // Light Green
                case "transport":
                    return 0xFFBBDEFB; // Light Blue
                case "entertainment":
                    return 0xFFD1C4E9; // Light Purple
                case "shopping":
                    return 0xFFFFF59D; // Light Yellow
                case "bills":
                    return 0xFFFFCDD2; // Light Red
                case "others":
                    return 0xFFFFCC80; // Light Orange
                default:
                    return 0xFFB0BEC5; // Light Gray (Fallback)
            }
        }
    }

    private void updateSummaryAndTips(Map<String, Float> categoryMap, String type) {
        float total = 0f;
        String highestCat = "";
        float highest = Float.MIN_VALUE;

        for (Map.Entry<String, Float> entry : categoryMap.entrySet()) {
            float amount = entry.getValue();
            total += amount;

            if (amount > highest) {
                highest = amount;
                highestCat = capitalize(entry.getKey());
            }
        }

        // Just update tips (since other summary views are not in XML)
        if (type.equals("expense")) {
            if (highest > total * 0.4) {
                tvTips.setText("💡 Tip: You are spending a lot on " + highestCat + ". Try to save!");
            } else if (total == 0) {
                tvTips.setText("💡 No expenses recorded yet. Add your expenses!");
            } else {
                tvTips.setText("💡 Great! Your spending looks balanced.");
            }
        } else {
            if (total == 0) {
                tvTips.setText("💡 No income recorded yet. Add your income!");
            } else {
                tvTips.setText("💡 Keep increasing your income streams!");
            }
        }
    }

    private void calculateBalance(float income, float expense) {
        float totalIncome = 0f, totalExpense = 0f;

        if (!tvIncome.getText().toString().equals("₹ 0")) {
            totalIncome = Float.parseFloat(tvIncome.getText().toString().replace("₹ ", "").replace(",", ""));
        }
        if (!tvExpense.getText().toString().equals("₹ 0")) {
            totalExpense = Float.parseFloat(tvExpense.getText().toString().replace("₹ ", "").replace(",", ""));
        }

        if (income != -1) totalIncome = income;
        if (expense != -1) totalExpense = expense;

        float balance = totalIncome - totalExpense;
        tvBalance.setText("₹ " + balance);
    }

}