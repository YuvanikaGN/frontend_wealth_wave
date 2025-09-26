package com.simats.wealth_wave;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.simats.wealth_wave.models.Plan;
import com.simats.wealth_wave.responses.PlanResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllPlansActivity extends AppCompatActivity {

    private static final String TAG = "AllPlans";
    private LinearLayout plansContainer;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_plans);

        plansContainer = findViewById(R.id.plansContainer);

        // ✅ Get userId from intent
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1); // fallback -1 if not found

        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchPlans(); // Fetch all plans of this user
    }



    private void addPlanCard(String title, String target, int progress) {
        CardView cardView = (CardView) getLayoutInflater()
                .inflate(R.layout.item_card_plan, plansContainer, false);

        TextView fundTitle = cardView.findViewById(R.id.fundTitle);
        TextView fundTarget = cardView.findViewById(R.id.fundTarget);
        TextView progressText = cardView.findViewById(R.id.progressPercentageText);
        ProgressBar progressBar = cardView.findViewById(R.id.progressBar);

        fundTitle.setText(title);
        fundTarget.setText(target);
        progressText.setText(progress + "%");
        progressBar.setMax(100);
        progressBar.setProgress(progress);

        plansContainer.addView(cardView);

    }





    private int safeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private void fetchPlans() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PlanResponse> call = apiService.getPlans(userId);

        call.enqueue(new Callback<PlanResponse>() {
            @Override
            public void onResponse(Call<PlanResponse> call, Response<PlanResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AllPlansActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    android.util.Log.e(TAG, "Response not successful or body null. Code=" + response.code());
                    return;
                }

                PlanResponse planResponse = response.body();
                android.util.Log.d(TAG, "API status=" + planResponse.isStatus() + " message=" + planResponse.getMessage());

                if (!planResponse.isStatus()) {
                    Toast.makeText(AllPlansActivity.this, planResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Plan> plans = planResponse.getData();
                if (plans == null || plans.isEmpty()) {
                    android.util.Log.d(TAG, "Plans list empty");
                    showEmptyStateCard(); // optional UI
                    return;
                }

                android.util.Log.d(TAG, "Plans found: " + plans.size());

                for (Plan p : plans) {
                    int target = safeInt(p.getTarget_amount());
                    int approx = safeInt(p.getIncome()); // or use approx_money if that’s the correct field
                    int progress = 0;
                    if (target > 0) {
                        progress = (int) Math.round((approx * 100.0) / target);
                        if (progress < 0) progress = 0;
                        if (progress > 100) progress = 100;
                    }

                    android.util.Log.d(TAG, "Plan: " + p.getGoal() + " target=" + target + " approx=" + approx + " progress=" + progress);
                    addPlanCard(p.getGoal(), "Target: " + p.getTarget_amount(), progress);
                }
            }

            @Override
            public void onFailure(Call<PlanResponse> call, Throwable t) {
                Toast.makeText(AllPlansActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e(TAG, "API failed", t);
            }
        });
    }

    private void showEmptyStateCard() {
        TextView tv = new TextView(this);
        tv.setText("No plans yet.");
        tv.setTextSize(16f);
        tv.setPadding(24, 24, 24, 24);
        plansContainer.addView(tv);
    }


}
