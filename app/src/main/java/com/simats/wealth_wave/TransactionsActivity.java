package com.simats.wealth_wave;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.DrawableRes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simats.wealth_wave.models.AddSavingRequest;
import com.simats.wealth_wave.models.AddSavingRequestWithPlan;
import com.simats.wealth_wave.models.DeleteTransactionRequest;
import com.simats.wealth_wave.models.SavingItem;
import com.simats.wealth_wave.models.TransactionRequest;
import com.simats.wealth_wave.responses.AddSavingResponse;
import com.simats.wealth_wave.responses.AddSavingResponseWithPlan;
import com.simats.wealth_wave.responses.DeleteTransactionResponse;
import com.simats.wealth_wave.responses.GetAllPlansResponse;
import com.simats.wealth_wave.responses.TransactionResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.retrofit.RetrofitClient;
import com.simats.wealth_wave.ui.LoginPageActivity;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View menuIcon, backArrow;
    private NavigationView navigationView;

    private AppCompatButton incomeBtn, expenseBtn, addBtn;
    private EditText etTransAmount, etTransCategory, etTransMode, etTransNote;
    private LinearLayout transHistory;

    private final NumberFormat indCurrency = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    // storage
    private SharedPreferences prefs;
    private Gson gson;
    private List<Transaction> transactions;
    private int selectedPlanId = -1;
    private int planOwnerId = -1;
    private static final String USER_PREFS_NAME = "UserPrefs"; // match LoginPageActivity



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

        SharedPreferences planPrefs = getSharedPreferences("WealthWavePrefs", MODE_PRIVATE);
        selectedPlanId = planPrefs.getInt("selected_plan_id", -1);
        planOwnerId = planPrefs.getInt("plan_owner_user_id", -1);

        if (selectedPlanId == -1 || planOwnerId == -1) {
            Toast.makeText(this, "Please select a plan first!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        prefs = getSharedPreferences("transactions_db", MODE_PRIVATE);

        // ✅ Get logged-in userId from SharedPreferences
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);

        gson = new Gson();

        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);

// Get references
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);

// Fetch from SharedPreferences (saved earlier after login/profile fetch)
        SharedPreferences prefs = getSharedPreferences(USER_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "User Name");
        String email = prefs.getString("email", "user@gmail.com");

// Set them
        navUserName.setText(name);
        navUserEmail.setText(email);

        // --- drawer / nav ---
        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        navigationView = findViewById(R.id.navigationView);
        backArrow = findViewById(R.id.backArrow);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
                    else drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // highlight "Home" when we are in Home screen
        bottomNavigationView.setSelectedItemId(R.id.nav_home_bot);

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


        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);

                if (id == R.id.nav_home) {
                    startActivity(new Intent(TransactionsActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_savings_plan) {
                    // Already here, do nothing
                } else if (id == R.id.nav_investment) {
                    startActivity(new Intent(TransactionsActivity.this, ProgressTrackingActivity.class));
                }
//                else if (id == R.id.nav_income) {
//                    startActivity(new Intent(TransactionsActivity.this, IncomeActivity.class));
//                }

                else if (id == R.id.nav_chatbot) {
                    startActivity(new Intent(TransactionsActivity.this, ChatbotActivity.class));
                }
                else if (id == R.id.nav_transactions) {
                    startActivity(new Intent(TransactionsActivity.this, TransactionsActivity.class));
                } else if (id == R.id.nav_edit_savings) {
                    startActivity(new Intent(TransactionsActivity.this, EditSavingsActivity.class));
                } else if (id == R.id.nav_logout) {
                    startActivity(new Intent(TransactionsActivity.this, LogoPageActivity.class));
                    finish();
                }
                overridePendingTransition(0, 0);
                return true;
            });
        }

        if (backArrow != null) {
            backArrow.setOnClickListener(v -> {
                startActivity(new Intent(TransactionsActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        // --- form views ---
        incomeBtn = findViewById(R.id.incomeBtn);
        expenseBtn = findViewById(R.id.expenseBtn);
        addBtn = findViewById(R.id.addBtn);

        etTransAmount = findViewById(R.id.etTransAmount);
        etTransCategory = findViewById(R.id.etTransCategory);
        etTransMode = findViewById(R.id.etTransMode);
        etTransNote = findViewById(R.id.etTransNote);

        transHistory = findViewById(R.id.transHistory);

        setTypeSelected(true);

        if (incomeBtn != null) {
            incomeBtn.setOnClickListener(v -> setTypeSelected(true));
        }
        if (expenseBtn != null) {
            expenseBtn.setOnClickListener(v -> {
                Intent intent = new Intent(TransactionsActivity.this, ExpActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (addBtn != null) {
            addBtn.setOnClickListener(v -> onAddClicked());
        }

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));

        if (transactions == null) {
            transactions = new ArrayList<>();
        } else {
            transactions.clear();
        }
        transHistory.removeAllViews();


        // 🔥 Load saved transactions
        loadTransactions();


        EditText etTransCategory = findViewById(R.id.etTransCategory);

        String[] expenseCategories = {"Salary", "Freelance", "Gifts", "Others"};

// when user clicks the category box
        etTransCategory.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Source of income");

            // choose based on transaction type (you can manage with a flag)
            builder.setItems(expenseCategories, (dialog, which) -> {
                etTransCategory.setText(expenseCategories[which]);
            });

            builder.show();
        });

        EditText etTransMode = findViewById(R.id.etTransMode);

        String[] modes = {"Bank", "Cash"};

// when user clicks the category box
        etTransMode.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Payment mode");

            // choose based on transaction type (you can manage with a flag)
            builder.setItems(modes, (dialog, which) -> {
                etTransMode.setText(modes[which]);
            });

            builder.show();
        });

        Intent intent = new Intent("com.simats.wealth_wave.UPDATE_SAVINGS");
        LocalBroadcastManager.getInstance(TransactionsActivity.this).sendBroadcast(intent);

    }

    private void showPlansForSaving(double transactionAmount) {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            showToast("User not logged in!");
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<GetAllPlansResponse> call = api.getPlansWithApprox(userId); // Use the same API as HomeActivity
        call.enqueue(new Callback<GetAllPlansResponse>() {
            @Override
            public void onResponse(Call<GetAllPlansResponse> call, Response<GetAllPlansResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<GetAllPlansResponse.Data> plans = response.body().getData();
                    if (plans.isEmpty()) {
                        showToast("No plans found for saving");
                        return;
                    }

                    // Show plans in a dialog
                    String[] planTitles = new String[plans.size()];
                    for (int i = 0; i < plans.size(); i++) planTitles[i] = "🎯 " + plans.get(i).getGoal();

                    new AlertDialog.Builder(TransactionsActivity.this)
                            .setTitle("Select a plan to save")
                            .setItems(planTitles, (dialog, which) -> {
                                GetAllPlansResponse.Data selectedPlan = plans.get(which);
                                askSavingAmount(transactionAmount, selectedPlan.getId());
                            })
                            .show();
                } else {
                    showToast("No plans available");
                }
            }

            @Override
            public void onFailure(Call<GetAllPlansResponse> call, Throwable t) {
                showToast("Error fetching plans: " + t.getMessage());
            }
        });
    }

    private void askSavingAmount(double transactionAmount, int planId) {
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter amount to save (≤ " + transactionAmount + ")");

        new AlertDialog.Builder(this)
                .setTitle("Enter Saving Amount")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = input.getText().toString().trim();
                    if (val.isEmpty()) {
                        showToast("Amount cannot be empty");
                        return;
                    }
                    double savingAmount = 0;
                    try { savingAmount = Double.parseDouble(val); } catch (Exception e) { showToast("Invalid amount"); return; }
                    if (savingAmount <= 0 || savingAmount > transactionAmount) {
                        showToast("Amount must be ≤ transaction amount");
                        return;
                    }

                    saveToSavingsBackend(planId, savingAmount);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveToSavingsBackend(int planId, double amount) {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            showToast("User not logged in!");
            return;
        }

        AddSavingRequest request = new AddSavingRequest(userId, planId, amount);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.addSaving(request).enqueue(new Callback<AddSavingResponse>() {
            @Override
            public void onResponse(Call<AddSavingResponse> call, Response<AddSavingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isOk()) {

                    // ✅ Save backend response
                    AddSavingResponse.Data data = response.body().getData();

                    // 🔥 Instead of only saving locally, also update backend-linked list
                    // If SavingsActivity fetches via getAllSavings(userId),
                    // then this record will already appear there automatically!

                    // Local cache (optional)
                    saveSavingLocally(userPrefs, planId, data.getId(), amount, data.getInserted_at());

                    // Notify UI
                    Intent intent = new Intent("com.simats.wealth_wave.UPDATE_SAVINGS");
                    LocalBroadcastManager.getInstance(TransactionsActivity.this).sendBroadcast(intent);

                    showToast("₹" + amount + " saved to plan successfully!");
                } else {
                    showToast("Failed to save to plan");
                }
            }

            @Override
            public void onFailure(Call<AddSavingResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void saveSavingLocally(SharedPreferences userPrefs, int planId, int savingId, double amount, String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        }

        double totalSaved = Double.longBitsToDouble(
                userPrefs.getLong("total_saved_" + planId, Double.doubleToLongBits(0))
        );
        totalSaved += amount;

        int historySize = userPrefs.getInt("total_saved_count_" + planId, 0);

        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putLong("total_saved_" + planId, Double.doubleToRawLongBits(totalSaved));
        editor.putLong("saved_item_" + planId + "_" + historySize, Double.doubleToRawLongBits(amount));
        editor.putString("saved_item_date_" + planId + "_" + historySize, dateTime);
        editor.putInt("saved_item_id_" + planId + "_" + historySize, savingId);
        editor.putInt("total_saved_count_" + planId, historySize + 1);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions(); // fetch latest from server
    }



    @DrawableRes
    private int iconForMode(String mode) {
        if (mode == null) return R.drawable.ic_wallet;
        String m = mode.trim().toLowerCase(Locale.ROOT);

        if (m.startsWith("cash")) return R.drawable.ic_cash;
        if (m.startsWith("bank")) return R.drawable.ic_bank;

        return R.drawable.ic_wallet; // fallback
    }


    private void setTypeSelected(boolean incomeSelected) {
        if (incomeBtn != null && expenseBtn != null) {
            incomeBtn.setSelected(incomeSelected);
            expenseBtn.setSelected(!incomeSelected);

            try {
                incomeBtn.setBackgroundResource(incomeSelected ? R.drawable.rounded_button : R.drawable.outline);
                expenseBtn.setBackgroundResource(!incomeSelected ? R.drawable.rounded_button : R.drawable.outline);
            } catch (Exception ignored) { }
        }
    }


    private void onAddClicked() {
        if (etTransAmount == null || etTransCategory == null || etTransMode == null || etTransNote == null) {
            showToast("Some input fields are missing!");
            return;
        }

        String rawAmount = etTransAmount.getText().toString().trim();
        String category = etTransCategory.getText().toString().trim();
        String mode = etTransMode.getText().toString().trim();
        String note = etTransNote.getText().toString().trim();

        double amount = parseAmount(rawAmount);
        if (amount <= 0) {
            showToast("Please enter a valid amount");
            return;
        }

        boolean isIncome = incomeBtn != null && incomeBtn.isSelected();

        // Get logged-in user ID
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            showToast("User not logged in!");
            return;
        }

        // Prepare transaction request for backend
        TransactionRequest request = new TransactionRequest(
                userId,
                amount,
                category,
                mode,
                note,
                isIncome ? 1 : 0
        );

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<TransactionResponse> call = api.addTransaction(request);

        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().ok) {
                    showToast("Transaction saved");

                    Transaction t = new Transaction(
                            response.body().id,
                            indCurrency.format(amount),
                            category,
                            mode,
                            note,
                            response.body().created_at,
                            isIncome
                    );

                    if (transactions == null) transactions = new ArrayList<>();
                    transactions.add(0, t);
                    saveTransactions(); // Save all transactions locally

                    addTransactionCard(t);

                    // Clear fields
                    etTransAmount.setText("");
                    etTransCategory.setText("");
                    etTransMode.setText("");
                    etTransNote.setText("");

                    // Broadcast update to other activities
                    Intent intent = new Intent("com.simats.wealth_wave.UPDATE_TRANSACTIONS");
                    LocalBroadcastManager.getInstance(TransactionsActivity.this).sendBroadcast(intent);

                    if (isIncome) showPlansForSaving(amount);
                }

                else {
                    showToast("Failed: " + (response.body() != null ? response.body().message : "Unknown error"));
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }


    private void loadTransactions() {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            showToast("User not logged in!");
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<com.simats.wealth_wave.models.Transaction>> call = api.getTransactions(userId);

        call.enqueue(new Callback<List<com.simats.wealth_wave.models.Transaction>>() {
            @Override
            public void onResponse(Call<List<com.simats.wealth_wave.models.Transaction>> call, Response<List<com.simats.wealth_wave.models.Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactions.clear();
                    for (com.simats.wealth_wave.models.Transaction t : response.body()) {
                        transactions.add(new Transaction(
                                t.getId(),
                                indCurrency.format(t.getAmount()),
                                t.getCategory(),
                                t.getMode(),
                                t.getNote(),
                                t.getCreatedAt(),
                                t.isIncome() == 1
                        ));
                    }

                    // Update UI
                    if (transHistory != null) transHistory.removeAllViews();
                    for (Transaction t : transactions) addTransactionCard(t);

                    // Save locally as cache
                    saveTransactions();
                } else {
                    showToast("Failed to load transactions from server.");
                    loadTransactionsFromCache(); // fallback
                }
            }

            @Override
            public void onFailure(Call<List<com.simats.wealth_wave.models.Transaction>> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
                loadTransactionsFromCache(); // fallback
            }
        });
    }

    private void loadTransactionsFromCache() {
        if (prefs == null) prefs = getSharedPreferences("transactions_db", MODE_PRIVATE);
        if (transactions == null) transactions = new ArrayList<>();

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) return;

        String key = "transactions_" + userId;
        String json = prefs.getString(key, "[]");

        try {
            Type listType = new TypeToken<ArrayList<Transaction>>() {}.getType();
            List<Transaction> loaded = gson.fromJson(json, listType);
            if (loaded != null) transactions = loaded;
        } catch (Exception e) {
            transactions = new ArrayList<>();
        }

        // Update UI
        if (transHistory != null) transHistory.removeAllViews();
        for (Transaction t : transactions) addTransactionCard(t);
    }

    private void saveTransactions() {
        if (prefs == null) prefs = getSharedPreferences("transactions_db", MODE_PRIVATE);
        if (transactions == null) transactions = new ArrayList<>();

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) return;

        String key = "transactions_" + userId;
        try {
            String json = gson.toJson(transactions);
            prefs.edit().putString(key, json).apply();
        } catch (Exception ignored) {}
    }


    private double parseAmount(String raw) {
        String cleaned = raw.replaceAll("[^0-9.,]", "").replace(",", "");
        if (cleaned.isEmpty()) return 0;
        try {
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    private int colorForCategory(String category) {
        if (category == null) return getResources().getColor(android.R.color.darker_gray);

        String c = category.trim().toLowerCase(Locale.ROOT);

        switch (c.toLowerCase()) {
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
    }


    private void addTransactionCard(Transaction t) {
        if (transHistory == null) return;

        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.item_transaction, transHistory, false);

        TextView tvAmount = card.findViewById(R.id.tvAmount);
        TextView tvSubtitle = card.findViewById(R.id.tvSubtitle);
        TextView tvBadge = card.findViewById(R.id.tvBadge);
        ImageView ivMode = card.findViewById(R.id.ivMode);

        if (tvAmount != null) tvAmount.setText(t.amount);

        StringBuilder subtitle = new StringBuilder();
        subtitle.append(t.category).append(" • ").append(t.mode).append(" • ").append(t.dateTime);
        if (!TextUtils.isEmpty(t.note)) subtitle.append(" • ").append(t.note);
        if (tvSubtitle != null) tvSubtitle.setText(subtitle.toString());

        if (tvBadge != null) {
            // show category instead of just "Income/Expense"
            tvBadge.setText(t.category);

            // set color based on category
            try {
                int catColor = colorForCategory(t.category);
                tvBadge.setBackgroundColor(catColor);
            } catch (Exception ignored) {}
        }


        // 🔥 Set the correct icon here (Bank/Cash/etc.)
        if (ivMode != null) {
            ivMode.setImageResource(iconForMode(t.mode));
            ivMode.setContentDescription(t.mode);
        }

        // delete with confirmation (unchanged)
        card.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteTransactionFromServer(t, card))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });


        transHistory.addView(card, 0);
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // model
    static class Transaction {
        public int serverId; // ID from the server
        String amount, category, mode, note, dateTime;
        boolean isIncome;

        // Correct constructor
        Transaction(int serverId, String amount, String category, String mode, String note, String dateTime, boolean isIncome) {
            this.serverId = serverId;
            this.amount = amount;
            this.category = category;
            this.mode = mode;
            this.note = note;
            this.dateTime = dateTime;
            this.isIncome = isIncome;
        }
    }




    private void deleteTransactionFromServer(Transaction t, View card) {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            showToast("User not logged in!");
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        DeleteTransactionRequest request = new DeleteTransactionRequest(userId, t.serverId);

        Call<DeleteTransactionResponse> call = api.deleteTransaction(request);
        call.enqueue(new Callback<DeleteTransactionResponse>() {
            @Override
            public void onResponse(Call<DeleteTransactionResponse> call, Response<DeleteTransactionResponse> response) {
                transHistory.removeView(card);
                transactions.remove(t);
                saveTransactions();

                if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                    showToast("Transaction deleted ID: " + response.body().getDeletedId());
                } else {
                    showToast("Deleted locally (not found in server)");
                }

                // Broadcast update
                Intent intent = new Intent("com.simats.wealth_wave.UPDATE_TRANSACTIONS");
                LocalBroadcastManager.getInstance(TransactionsActivity.this).sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<DeleteTransactionResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

}