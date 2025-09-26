package com.simats.wealth_wave;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.wealth_wave.models.DeleteSavingRequestWithPlan;
import com.simats.wealth_wave.responses.DeleteSavingResponseWithPlan;
import com.simats.wealth_wave.models.SavingItem;
import com.simats.wealth_wave.models.SavingsAdapter;
import com.simats.wealth_wave.retrofit.RetrofitClient;

import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

public class SavingsHistoryActivity extends AppCompatActivity {

    private RecyclerView rvFullSavingsHistory;
    private SavingsAdapter adapter;
    private List<SavingItem> savingsList = new ArrayList<>();
    private int planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savings_history);

        rvFullSavingsHistory = findViewById(R.id.rvFullSavingsHistory);
        rvFullSavingsHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavingsAdapter(savingsList);

        // Set long-click listener for deleting items
        adapter.setOnItemLongClickListener((position, item) -> {
            confirmDelete(position, item);
        });

        rvFullSavingsHistory.setAdapter(adapter);

        planId = getIntent().getIntExtra("planId", -1);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));

        loadAllSavingsHistory();
    }

    /** Load all savings and transactions */
    private void loadAllSavingsHistory() {
        if (planId == -1) return;

        savingsList.clear();
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        int savingsCount = userPrefs.getInt("total_saved_count_" + planId, 0);
        int txnCount = userPrefs.getInt("total_transaction_count_" + planId, 0);

        // Load savings
        for (int i = 0; i < savingsCount; i++) {
            double amount = Double.longBitsToDouble(
                    userPrefs.getLong("saved_item_" + planId + "_" + i, Double.doubleToLongBits(0))
            );
            String date = userPrefs.getString("saved_item_date_" + planId + "_" + i, "");
            int id = userPrefs.getInt("saved_item_id_" + planId + "_" + i, -1);
            savingsList.add(new SavingItem(id, amount, date));
        }

        // Load transactions
        for (int i = 0; i < txnCount; i++) {
            double amount = Double.longBitsToDouble(
                    userPrefs.getLong("transaction_item_" + planId + "_" + i, Double.doubleToLongBits(0))
            );
            String date = userPrefs.getString("transaction_item_date_" + planId + "_" + i, "");
            int id = userPrefs.getInt("transaction_item_id_" + planId + "_" + i, -1);
            savingsList.add(new SavingItem(id, amount, date));
        }

        // Sort by date descending
        savingsList.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));

        adapter.notifyDataSetChanged();
    }

    /** Show confirmation dialog before deletion */
    private void confirmDelete(int position, SavingItem item) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Saving")
                .setMessage("Do you want to delete this saving?")
                .setPositiveButton("Yes", (dialog, which) -> deleteSaving(position, item))
                .setNegativeButton("No", null)
                .show();
    }

    /** Delete item locally and from server */
    private void deleteSaving(int position, SavingItem item) {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Remove locally
        int savingsCount = userPrefs.getInt("total_saved_count_" + planId, 0);
        int txnCount = userPrefs.getInt("total_transaction_count_" + planId, 0);

        boolean isSavedItem = false;

        // Check saved items
        for (int i = 0; i < savingsCount; i++) {
            int id = userPrefs.getInt("saved_item_id_" + planId + "_" + i, -1);
            if (id == item.getId()) {
                isSavedItem = true;
                SharedPreferences.Editor editor = userPrefs.edit();
                for (int j = i; j < savingsCount - 1; j++) {
                    editor.putLong("saved_item_" + planId + "_" + j,
                            userPrefs.getLong("saved_item_" + planId + "_" + (j + 1), 0));
                    editor.putString("saved_item_date_" + planId + "_" + j,
                            userPrefs.getString("saved_item_date_" + planId + "_" + (j + 1), ""));
                    editor.putInt("saved_item_id_" + planId + "_" + j,
                            userPrefs.getInt("saved_item_id_" + planId + "_" + (j + 1), -1));
                }
                editor.remove("saved_item_" + planId + "_" + (savingsCount - 1));
                editor.remove("saved_item_date_" + planId + "_" + (savingsCount - 1));
                editor.remove("saved_item_id_" + planId + "_" + (savingsCount - 1));
                editor.putInt("total_saved_count_" + planId, savingsCount - 1);
                editor.apply();
                break;
            }
        }

        // Check transactions
        if (!isSavedItem) {
            SharedPreferences.Editor editor = userPrefs.edit();
            for (int i = 0; i < txnCount; i++) {
                int id = userPrefs.getInt("transaction_item_id_" + planId + "_" + i, -1);
                if (id == item.getId()) {
                    for (int j = i; j < txnCount - 1; j++) {
                        editor.putLong("transaction_item_" + planId + "_" + j,
                                userPrefs.getLong("transaction_item_" + planId + "_" + (j + 1), 0));
                        editor.putString("transaction_item_date_" + planId + "_" + j,
                                userPrefs.getString("transaction_item_date_" + planId + "_" + (j + 1), ""));
                        editor.putInt("transaction_item_id_" + planId + "_" + j,
                                userPrefs.getInt("transaction_item_id_" + planId + "_" + (j + 1), -1));
                    }
                    editor.remove("transaction_item_" + planId + "_" + (txnCount - 1));
                    editor.remove("transaction_item_date_" + planId + "_" + (txnCount - 1));
                    editor.remove("transaction_item_id_" + planId + "_" + (txnCount - 1));
                    editor.putInt("total_transaction_count_" + planId, txnCount - 1);
                    editor.apply();
                    break;
                }
            }
        }

        // Remove from list & update adapter
        savingsList.remove(position);
        adapter.notifyItemRemoved(position);

        // Delete from server
        RetrofitClient.getInstance().getApi()
                .deleteSaving(new DeleteSavingRequestWithPlan(item.getId(), planId))
                .enqueue(new retrofit2.Callback<DeleteSavingResponseWithPlan>() {
                    @Override
                    public void onResponse(Call<DeleteSavingResponseWithPlan> call, retrofit2.Response<DeleteSavingResponseWithPlan> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                            Toast.makeText(SavingsHistoryActivity.this, "Saving deleted from server ✅", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SavingsHistoryActivity.this, "Server deletion failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteSavingResponseWithPlan> call, Throwable t) {
                        Toast.makeText(SavingsHistoryActivity.this, "Failed to delete from server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
