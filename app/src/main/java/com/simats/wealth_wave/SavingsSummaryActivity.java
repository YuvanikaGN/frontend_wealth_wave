package com.simats.wealth_wave;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.simats.wealth_wave.ui.LoginPageActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SavingsSummaryActivity extends AppCompatActivity {

    private TextView tvGoalName, tvSavedAmount, tvTargetAmount, tvProgress,
            tvLastSaved, tvAvgMonthly, tvRemaining, tvStartDate;
    private ImageView menuIcon;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private NavigationView navigationView;
    private AppCompatButton downloadPdfButton,backToProfileBtn,sharePdfButton;
    private LinearLayout pdfContent;


    private static final String USER_PREFS_NAME = "UserPrefs";
    private static final String PREF_NAME = "WealthWavePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_summary);

        // Bind views
        tvGoalName = findViewById(R.id.tvGoalName);
        tvSavedAmount = findViewById(R.id.tvSavedAmount);
        tvTargetAmount = findViewById(R.id.tvTargetAmount);
        tvProgress = findViewById(R.id.tvProgress);
        tvLastSaved = findViewById(R.id.tvLastSaved);
        tvAvgMonthly = findViewById(R.id.tvAvgMonthly);
        tvRemaining = findViewById(R.id.tvRemaining);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tvStartDate = findViewById(R.id.tvStartDate);


//        downloadPdfButton = findViewById(R.id.downloadPdfButton);
        pdfContent = findViewById(R.id.pdfContent);

//        downloadPdfButton.setOnClickListener(v -> {
//            createPDF(pdfContent);
//        });

        sharePdfButton = findViewById(R.id.sharePdfButton);
        sharePdfButton.setOnClickListener(v -> generateProfessionalPdf());



        backToProfileBtn = findViewById(R.id.backToProfileBtn);
        backToProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(SavingsSummaryActivity.this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
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

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_savings_plan) startActivity(new Intent(SavingsSummaryActivity.this, SavingsActivity.class));
            else if (id == R.id.nav_investment) startActivity(new Intent(SavingsSummaryActivity.this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_transactions) startActivity(new Intent(SavingsSummaryActivity.this, TransactionsActivity.class));
            else if (id == R.id.nav_edit_savings) startActivity(new Intent(SavingsSummaryActivity.this, EditSavingsActivity.class));
            else if (id == R.id.nav_chatbot) startActivity(new Intent(SavingsSummaryActivity.this, ChatbotActivity.class));
            else if (id == R.id.nav_logout) {
                startActivity(new Intent(SavingsSummaryActivity.this, LoginPageActivity.class));
                finish();
            }
            overridePendingTransition(0, 0);
            return true;
        });

        refreshSavingsSummaryUI();

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences planPrefs = getSharedPreferences("WealthWavePrefs", MODE_PRIVATE);

// Example: Get selected plan id
        int planId = planPrefs.getInt("selected_plan_id", -1);
        if(planId == -1) return;

// Total saved
        double totalSaved = Double.longBitsToDouble(userPrefs.getLong("total_saved_" + planId, Double.doubleToLongBits(0)));
        TextView tvSavedAmount = findViewById(R.id.tvSavedAmount);
        tvSavedAmount.setText(NumberFormat.getCurrencyInstance(new Locale("en","IN")).format(totalSaved));

// Last saved
        double lastSaved = Double.longBitsToDouble(userPrefs.getLong("last_saved_amount_" + planId, Double.doubleToLongBits(0)));
        String lastSavedDate = userPrefs.getString("last_saved_date_" + planId, "N/A");
        TextView tvLastSaved = findViewById(R.id.tvLastSaved);
        tvLastSaved.setText(lastSavedDate);

// Contributions
        int contributions = userPrefs.getInt("total_saved_count_" + planId, 0);
        TextView tvContributions = findViewById(R.id.tvContributions);
        tvContributions.setText(String.valueOf(contributions));

// Highest single saving
        double highest = 0;
        for(int i = 0; i < contributions; i++){
            double val = Double.longBitsToDouble(userPrefs.getLong("saved_item_" + planId + "_" + i, Double.doubleToLongBits(0)));
            if(val > highest) highest = val;
        }
        TextView tvHighest = findViewById(R.id.tvHighestSaving);
        tvHighest.setText(NumberFormat.getCurrencyInstance(new Locale("en","IN")).format(highest));

// Remaining to goal
        double target = Double.longBitsToDouble(userPrefs.getLong("plan_target_" + planId, Double.doubleToLongBits(0)));
        TextView tvRemaining = findViewById(R.id.tvRemaining);
        tvRemaining.setText(NumberFormat.getCurrencyInstance(new Locale("en","IN")).format(target - totalSaved));

// Progress %
        TextView tvProgress = findViewById(R.id.tvProgress);
        int progress = (target > 0) ? (int) ((totalSaved / target) * 100) : 0;
        tvProgress.setText(progress + "%");

        TextView dateText = findViewById(R.id.dateText);

// Get current date
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());

// Set it to dateText
        dateText.setText(currentDate);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }

    private void generateProfessionalPdf() {
        PdfDocument document = new PdfDocument();

        // A4 size
        int pageWidth = 595;
        int pageHeight = 842;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint titlePaint = new Paint();
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(20);
        titlePaint.setColor(Color.BLACK);

        Paint headerPaint = new Paint();
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setTextSize(14);
        headerPaint.setColor(Color.DKGRAY);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(14);
        normalPaint.setColor(Color.BLACK);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(2);

        int x = 40;
        int y = 60;

        // Company/App Header
        canvas.drawText("Wealth Wave", x, y, titlePaint);
        y += 20;
        canvas.drawLine(x, y, pageWidth - x, y, linePaint);
        y += 40;

        // Report Title + Date
        canvas.drawText("Savings Summary Report", x, y, titlePaint);
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        canvas.drawText("Date: " + currentDate, pageWidth - 150, y, normalPaint);
        y += 30;

        // Section 1: Goal Info
        canvas.drawText("Goal Information", x, y, headerPaint);
        y += 25;

        canvas.drawText("Goal Name: ", x, y, headerPaint);
        canvas.drawText(tvGoalName.getText().toString(), x + 150, y, normalPaint);
        y += 20;

        canvas.drawText("Target Amount: ", x, y, headerPaint);
        canvas.drawText(tvTargetAmount.getText().toString(), x + 150, y, normalPaint);
        y += 20;

        canvas.drawText("Progress: ", x, y, headerPaint);
        canvas.drawText(tvProgress.getText().toString(), x + 150, y, normalPaint);
        y += 20;

        canvas.drawText("Remaining: ", x, y, headerPaint);
        canvas.drawText(tvRemaining.getText().toString(), x + 150, y, normalPaint);
        y += 40;

        // Section 2: Savings Details
        canvas.drawText("Savings Details", x, y, headerPaint);
        y += 25;

        canvas.drawText("Total Saved: ", x, y, headerPaint);
        canvas.drawText(tvSavedAmount.getText().toString(), x + 150, y, normalPaint);
        y += 20;

        canvas.drawText("Last Saved: ", x, y, headerPaint);
        canvas.drawText(tvLastSaved.getText().toString(), x + 150, y, normalPaint);
        y += 20;

        TextView tvContributions = findViewById(R.id.tvContributions);
        canvas.drawText("Contributions: ", x, y, headerPaint);
        canvas.drawText(tvContributions.getText().toString(), x + 150, y, normalPaint);
        y += 20;

        TextView tvHighestSaving = findViewById(R.id.tvHighestSaving);
        canvas.drawText("Highest Saving: ", x, y, headerPaint);
        canvas.drawText(tvHighestSaving.getText().toString(), x + 150, y, normalPaint);
        y += 40;

        // Section 3: Other Info
        canvas.drawText("Additional Information", x, y, headerPaint);
        y += 25;

        canvas.drawText("End Date: ", x, y, headerPaint);
        canvas.drawText(tvStartDate.getText().toString(), x + 150, y, normalPaint);
        y += 20;

        TextView tvTrend = findViewById(R.id.tvTrend);
        canvas.drawText("Trend: ", x, y, headerPaint);
        canvas.drawText(tvTrend.getText().toString(), x + 150, y, normalPaint);
        y += 40;

        // Footer
        canvas.drawLine(x, pageHeight - 60, pageWidth - x, pageHeight - 60, linePaint);
        canvas.drawText("Generated by Wealth Wave App", x, pageHeight - 40, normalPaint);

        document.finishPage(page);

        // Save file
        File pdfDir = new File(getExternalFilesDir(null), "WealthWavePDFs");
        if (!pdfDir.exists()) pdfDir.mkdirs();

        String goalName = tvGoalName.getText().toString().trim();
        if (goalName.isEmpty()) goalName = "MyGoal";

// Clean filename (remove invalid chars for filesystems)
        String safeGoalName = goalName.replaceAll("[^a-zA-Z0-9\\s]", "").replace(" ", "_");

        String fileName = safeGoalName + "_Savings_Summary.pdf";

        File pdfFile = new File(pdfDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            document.close();
            fos.close();

            Uri pdfUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", pdfFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share PDF via"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private File getLatestPDF(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".pdf"));
        if (files == null || files.length == 0) return null;
        File latest = files[0];
        for (File file : files) {
            if (file.lastModified() > latest.lastModified()) {
                latest = file;
            }
        }
        return latest;
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshSavingsSummaryUI();
    }

    private void refreshSavingsSummaryUI() {
        SharedPreferences planPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);

        int selectedPlanId = planPrefs.getInt("selected_plan_id", -1);
        if (selectedPlanId == -1) {
            tvGoalName.setText("No plan selected");
            tvSavedAmount.setText("₹ 0.00");
            tvTargetAmount.setText("₹ 0.00");
            tvProgress.setText("0%");
            tvLastSaved.setText("N/A");
            tvAvgMonthly.setText("₹ 0.00");
            tvRemaining.setText("₹ 0.00");
            return;
        }

        // Read plan info
        String goalName = planPrefs.getString("selected_plan_goal", "My Goal");
        double totalSaved = getPlanSavedAmount(selectedPlanId);
        double targetAmount = getSafeDouble(planPrefs, "selected_plan_target");

        String endDate = planPrefs.getString("selected_plan_duration", "N/A");
        tvStartDate.setText(endDate);   // ✅ show end date instead


        // Last saved
        // Get last saved date & amount from saved history
        int totalSavedCount = userPrefs.getInt("total_saved_count_" + selectedPlanId, 0);
        double lastSavedAmount = 0;
        String lastSavedDate = "N/A";

        if (totalSavedCount > 0) {
            int lastIndex = totalSavedCount - 1;
            lastSavedAmount = Double.longBitsToDouble(
                    userPrefs.getLong("saved_item_" + selectedPlanId + "_" + lastIndex, Double.doubleToLongBits(0))
            );
            lastSavedDate = userPrefs.getString("saved_item_date_" + selectedPlanId + "_" + lastIndex, "N/A");
        }

        // Average monthly savings
        int planDurationMonths = getPlanDurationMonths(planPrefs);
        double avgMonthly = planDurationMonths > 0 ? totalSaved / planDurationMonths : 0;

        // Set UI
        tvGoalName.setText(goalName);
        tvSavedAmount.setText("₹ " + String.format("%,.2f", totalSaved));
        tvTargetAmount.setText("₹ " + String.format("%,.2f", targetAmount));
        tvLastSaved.setText(lastSavedAmount > 0 ?
                String.format("₹ %,.2f on %s", lastSavedAmount, lastSavedDate) : "N/A");
        tvAvgMonthly.setText("₹ " + String.format("%,.2f", avgMonthly));

        int progressPercent = targetAmount > 0 ? (int) ((totalSaved / targetAmount) * 100) : 0;
        if (progressPercent > 100) progressPercent = 100;

        ValueAnimator animator = ValueAnimator.ofInt(0, progressPercent);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            tvProgress.setText(value + "% reached");
        });
        animator.start();

        double remaining = targetAmount - totalSaved;
        if (remaining < 0) remaining = 0;
        tvRemaining.setText("₹ " + String.format("%,.2f", remaining));
    }

    private int getPlanDurationMonths(SharedPreferences planPrefs) {
        String durationStr = planPrefs.getString("selected_plan_duration", "0");
        try {
            return Integer.parseInt(durationStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
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

    private double getSafeDouble(SharedPreferences prefs, String key) {
        try {
            return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(0)));
        } catch (ClassCastException e) {
            float oldValue = prefs.getFloat(key, 0f);
            prefs.edit().putLong(key, Double.doubleToRawLongBits(oldValue)).apply();
            return oldValue;
        }
    }
}
