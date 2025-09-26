package com.simats.wealth_wave;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.models.UserDetailsRequest;
import com.simats.wealth_wave.responses.UserDetailsResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsActivity extends AppCompatActivity {

    private static final String USER_PREFS_NAME = "UserPrefs"; // same as login saves to

    private ImageView backArrow;
    private AppCompatButton createPlanBtn;
    private AppCompatButton homeBtn, vacationBtn, emergencyBtn, educationBtn;
    private EditText goalEditText, targetAmountEditText, monthlyIncomeEditText, durationEditText;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Elements
        backArrow = findViewById(R.id.backArrow);
        createPlanBtn = findViewById(R.id.createPlanBtn);
        homeBtn = findViewById(R.id.homeBtn);
        vacationBtn = findViewById(R.id.vacationBtn);
        emergencyBtn = findViewById(R.id.emergencyBtn);
        educationBtn = findViewById(R.id.educationBtn);
        goalEditText = findViewById(R.id.goalEditText);
        targetAmountEditText = findViewById(R.id.targetAmountEditText);
        monthlyIncomeEditText = findViewById(R.id.monthlyIncomeEditText);
        durationEditText = findViewById(R.id.durationEditText);

        // Back navigation
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(UserDetailsActivity.this, BankDetailsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // Calendar picker
        durationEditText.setOnClickListener(v -> showDatePicker());

        // Category buttons
        homeBtn.setOnClickListener(v -> {
            goalEditText.setText("Home Savings");
            highlightSelectedGoalButton(homeBtn);
        });

        vacationBtn.setOnClickListener(v -> {
            goalEditText.setText("Vacation Fund");
            highlightSelectedGoalButton(vacationBtn);
        });

        emergencyBtn.setOnClickListener(v -> {
            goalEditText.setText("Emergency Fund");
            highlightSelectedGoalButton(emergencyBtn);
        });

        educationBtn.setOnClickListener(v -> {
            goalEditText.setText("Education Fund");
            highlightSelectedGoalButton(educationBtn);
        });

        // Submit API call
        createPlanBtn.setOnClickListener(v -> submitUserDetails());
    }

    private void showDatePicker() {
        final Calendar today = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    if (selectedDate.before(today)) {
                        Toast.makeText(UserDetailsActivity.this, "Please select a future date.", Toast.LENGTH_SHORT).show();
                    } else {
                        durationEditText.setText(dateFormat.format(selectedDate.getTime()));
                    }
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        datePickerDialog.show();
    }

    private void highlightSelectedGoalButton(AppCompatButton selectedButton) {
        AppCompatButton[] buttons = {homeBtn, vacationBtn, emergencyBtn, educationBtn};

        for (AppCompatButton button : buttons) {
            button.setBackgroundTintList(ContextCompat.getColorStateList(this,
                    button == selectedButton ? R.color.selected_button_bg : R.color.default_button_bg));
            button.setTextColor(ContextCompat.getColor(this,
                    button == selectedButton ? R.color.blue : R.color.black));
        }
    }

    private void submitUserDetails() {
        String goal = goalEditText.getText().toString().trim();
        String targetAmountStr = targetAmountEditText.getText().toString().trim();
        String incomeStr = monthlyIncomeEditText.getText().toString().trim();
        String duration = durationEditText.getText().toString().trim();

        if (goal.isEmpty() || targetAmountStr.isEmpty() || incomeStr.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double targetAmount = Double.parseDouble(targetAmountStr);
            double income = Double.parseDouble(incomeStr);

            // Fetch user_id from SharedPreferences
            int userId = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE)
                    .getInt("user_id", 0);

            if (userId == 0) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create request with user_id
            UserDetailsRequest request = new UserDetailsRequest(goal, targetAmount, income, duration, userId);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<UserDetailsResponse> call = apiService.submitUserDetails(request);

            call.enqueue(new Callback<UserDetailsResponse>() {
                @Override
                public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(UserDetailsActivity.this,
                                response.body().getMessage() != null ? response.body().getMessage() : "Plan created successfully.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserDetailsActivity.this, PlanCreationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserDetailsActivity.this, "Failed to save details.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                    Toast.makeText(UserDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for amount and income.", Toast.LENGTH_SHORT).show();
        }
    }

}
