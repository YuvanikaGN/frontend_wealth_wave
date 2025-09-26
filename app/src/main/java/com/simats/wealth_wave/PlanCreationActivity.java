package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.wealth_wave.responses.UserPlanResponse;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.ui.LoginPageActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlanCreationActivity extends AppCompatActivity {

    private ImageView backArrow;
    private TextView back;
    private AppCompatButton savingsPlan;

    private TextView desiredAmt, monthlyTarget, timeline;

    private static final String USER_PREFS_NAME = "UserPrefs"; // match LoginPageActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.plan_creation);

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views
        backArrow = findViewById(R.id.backArrow);
        back = findViewById(R.id.back);
        savingsPlan = findViewById(R.id.savingsPlan);

        desiredAmt = findViewById(R.id.desiredAmt);
        monthlyTarget = findViewById(R.id.monthlyTarget);
        timeline = findViewById(R.id.timeline);

        // Back navigation
        backArrow.setOnClickListener(v -> navigateBack());
        back.setOnClickListener(v -> navigateBack());

        // Savings plan button
        savingsPlan.setOnClickListener(v -> {
            Intent intent = new Intent(PlanCreationActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Fetch plan from API
        fetchUserPlan();
    }

    private void navigateBack() {
        Intent intent = new Intent(PlanCreationActivity.this, UserDetailsActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void fetchUserPlan() {
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.24.93.232/app_database/") // replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Create body with user_id
        Map<String, Integer> body = new HashMap<>();
        body.put("user_id", userId);

        Call<UserPlanResponse> call = apiService.getUserPlan(body);

        call.enqueue(new Callback<UserPlanResponse>() {
            @Override
            public void onResponse(Call<UserPlanResponse> call, Response<UserPlanResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    UserPlanResponse plan = response.body();

                    // Set TextViews
                    desiredAmt.setText("₹" + String.format("%,.2f", plan.getTargetAmount()));
                    monthlyTarget.setText("₹" + String.format("%,.2f", plan.getApproxMoney()));
                    timeline.setText(plan.getDuration());

                } else {
                    Toast.makeText(PlanCreationActivity.this, "No plan found for this user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserPlanResponse> call, Throwable t) {
                Toast.makeText(PlanCreationActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
