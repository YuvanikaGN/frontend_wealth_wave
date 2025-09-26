package com.simats.wealth_wave;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.wealth_wave.retrofit.RetrofitClient;
import com.simats.wealth_wave.responses.GetAllPlansResponse;
import com.simats.wealth_wave.utils.PlansAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlansActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlansAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plans);

        recyclerView = findViewById(R.id.recyclerViewPlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get userId from SharedPreferences (like in edit savings)
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            loadPlans(userId);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPlans(int userId) {
        RetrofitClient.getInstance().getApi().getAllPlans(userId)
                .enqueue(new Callback<GetAllPlansResponse>() {
                    @Override
                    public void onResponse(Call<GetAllPlansResponse> call, Response<GetAllPlansResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                            List<GetAllPlansResponse.Data> plans = response.body().getData();
                            adapter = new PlansAdapter(plans);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(ViewPlansActivity.this, "No plans found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAllPlansResponse> call, Throwable t) {
                        Toast.makeText(ViewPlansActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
