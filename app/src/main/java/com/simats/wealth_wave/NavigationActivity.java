package com.simats.wealth_wave;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.wealth_wave.databinding.NavigationBinding;

public class NavigationActivity extends AppCompatActivity {

    NavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavigationBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home_bot) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_savings_plan_bot) {
                startActivity(new Intent(this, SavingsActivity.class));
            } else if (id == R.id.nav_investment_bot) {
                startActivity(new Intent(this, ProgressTrackingActivity.class));
            } else if (id == R.id.nav_profile_bot) {
                startActivity(new Intent(this, UserProfileActivity.class));
            }

            return true;
        });

    }
}
