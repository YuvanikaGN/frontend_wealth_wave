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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simats.wealth_wave.models.DeleteExpRequest;
import com.simats.wealth_wave.models.Expense;
import com.simats.wealth_wave.models.ExpRequest;
import com.simats.wealth_wave.models.Transaction;
import com.simats.wealth_wave.responses.DeleteExpResponse;
import com.simats.wealth_wave.responses.ExpResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.ui.LoginPageActivity;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View menuIcon, backArrow;
    private NavigationView navigationView;
    private AppCompatButton incomeBtn, expenseBtn, addBtn;
    private EditText etExpAmount, etExpCategory, etExpMode, etExpNote;
    private LinearLayout expenseHistory;
    private final NumberFormat indCurrency = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final String USER_PREFS_NAME = "UserPrefs"; // match LoginPageActivity


    private List<Expense> exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exp);

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPageActivity.class));
            finish();
            return;
        }

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
                    startActivity(new Intent(ExpActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_savings_plan) {
                    // Already here, do nothing
                } else if (id == R.id.nav_investment) {
                    startActivity(new Intent(ExpActivity.this, ProgressTrackingActivity.class));
                }
                else if (id == R.id.nav_chatbot) {
                    startActivity(new Intent(ExpActivity.this, ChatbotActivity.class));
                }
                else if (id == R.id.nav_transactions) {
                    startActivity(new Intent(ExpActivity.this, ExpActivity.class));
                } else if (id == R.id.nav_edit_savings) {
                    startActivity(new Intent(ExpActivity.this, EditSavingsActivity.class));
                } else if (id == R.id.nav_logout) {
                    startActivity(new Intent(ExpActivity.this, LogoPageActivity.class));
                    finish();
                }
                overridePendingTransition(0, 0);
                return true;
            });
        }

        if (backArrow != null) {
            backArrow.setOnClickListener(v -> {
                startActivity(new Intent(ExpActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        // --- form views ---
        incomeBtn = findViewById(R.id.incomeBtn);
        expenseBtn = findViewById(R.id.expenseBtn);
        addBtn = findViewById(R.id.addBtn);

        etExpAmount = findViewById(R.id.etExpAmount);
        etExpCategory = findViewById(R.id.etExpCategory);
        etExpMode = findViewById(R.id.etExpMode);
        etExpNote = findViewById(R.id.etExpNote);

        expenseHistory = findViewById(R.id.expenseHistory);

        setTypeSelected(true);

        if (expenseBtn != null) {
            expenseBtn.setOnClickListener(v -> setTypeSelected(true));
        }
        if (incomeBtn != null) {
            incomeBtn.setOnClickListener(v -> {
                Intent intent = new Intent(ExpActivity.this, TransactionsActivity.class);
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

        if (exp == null) {
            exp = new ArrayList<>();
        } else {
            exp.clear();
        }
        expenseHistory.removeAllViews();


        // 🔥 Load saved exp
        loadexp();


        EditText etExpCategory = findViewById(R.id.etExpCategory);

        String[] expenseCategories = {"Food", "Transport","Entertainment", "Shopping", "Bills", "Others"};

// when user clicks the category box
        etExpCategory.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Expense Category");

            // choose based on transaction type (you can manage with a flag)
            builder.setItems(expenseCategories, (dialog, which) -> {
                etExpCategory.setText(expenseCategories[which]);
            });

            builder.show();
        });

        EditText etExpMode = findViewById(R.id.etExpMode);

        String[] modes = {"Bank", "Cash"};

// when user clicks the category box
        etExpMode.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Payment mode");

            // choose based on transaction type (you can manage with a flag)
            builder.setItems(modes, (dialog, which) -> {
                etExpMode.setText(modes[which]);
            });

            builder.show();
        });

//        prefs.edit().clear().apply(); //use when you want to delete frontend


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadexp(); // reload expenses whenever we return
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
        if (expenseBtn != null && incomeBtn != null) {
            expenseBtn.setSelected(incomeSelected);
            incomeBtn.setSelected(!incomeSelected);

            try {
                expenseBtn.setBackgroundResource(incomeSelected ? R.drawable.rounded_button : R.drawable.outline);
                incomeBtn.setBackgroundResource(!incomeSelected ? R.drawable.rounded_button : R.drawable.outline);
            } catch (Exception ignored) { }
        }
    }


    private void onAddClicked() {
        String rawAmount = etExpAmount.getText().toString().trim();
        String category = etExpCategory.getText().toString().trim();
        String mode = etExpMode.getText().toString().trim();
        String note = etExpNote.getText().toString().trim();

        double amount = parseAmount(rawAmount);
        boolean isIncome = incomeBtn.isSelected();

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1); // 🔥 replace with actual logged-in userId from SharedPreferences/session

        ExpRequest request = new ExpRequest(
                userId,
                amount,
                category,
                mode,
                note,
                isIncome ? 1 : 0
        );

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ExpResponse> call = api.addExp(request);



        call.enqueue(new Callback<ExpResponse>() {
            @Override
            public void onResponse(Call<ExpResponse> call, Response<ExpResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().ok) {
                    showToast("Transaction saved");
                    // Optionally update local list
                    Expense t = new Expense(
                            response.body().id, // server ID from PHP
                            indCurrency.format(amount),
                            category,
                            mode,
                            note,
                            response.body().created_at,
                            isIncome
                    );


                    exp.add(0, t);
//                    saveexp();
                    addExpCard(t);

                    etExpAmount.setText("");
                    etExpCategory.setText("");
                    etExpMode.setText("");
                    etExpNote.setText("");

                } else {
                    showToast("Failed: " + (response.body() != null ? response.body().message : "Unknown error"));
                }
            }

            @Override
            public void onFailure(Call<ExpResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
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


    private void addExpCard(Expense t) {
        if (expenseHistory == null) return;

        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.item_transaction, expenseHistory, false);

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
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this Expense?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteExpFromServer(t, card))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });


        expenseHistory.addView(card, 0);
    }

    private void loadexp() {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<Transaction>> call = api.getExpense(userId);

        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> transactions = response.body();
                    exp.clear();
                    expenseHistory.removeAllViews();

                    for (Transaction t : transactions) {
                        Expense e = new Expense(
                                t.getId(),
                                indCurrency.format(t.getAmount()),
                                t.getCategory(),
                                t.getMode(),
                                t.getNote(),
                                t.getCreatedAt(),
                                t.isIncome() == 1
                        );
                        exp.add(e);
                        addExpCard(e);
                    }
                } else {
                    showToast("Failed to load expenses");
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // model
    static class Expense {
        public int serverId; // ID from the server
        String amount, category, mode, note, dateTime;
        boolean isIncome;

        // Correct constructor
        Expense(int serverId, String amount, String category, String mode, String note, String dateTime, boolean isIncome) {
            this.serverId = serverId;
            this.amount = amount;
            this.category = category;
            this.mode = mode;
            this.note = note;
            this.dateTime = dateTime;
            this.isIncome = isIncome;
        }
    }




    private void deleteExpFromServer(Expense t, View card) {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1); // replace with actual logged-in user ID
        ApiService api = ApiClient.getClient().create(ApiService.class);
        DeleteExpRequest request = new DeleteExpRequest(userId, t.serverId); // make sure Transaction has serverId

        Call<DeleteExpResponse> call = api.deleteExp(request);
        call.enqueue(new Callback<DeleteExpResponse>() {
            @Override
            public void onResponse(Call<DeleteExpResponse> call, Response<DeleteExpResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                    expenseHistory.removeView(card);
                    exp.remove(t);
//                    saveexp();
                    showToast("Transaction deleted ID: " + response.body().getDeletedId());
                } else {
                    showToast("Delete failed: " + (response.body() != null ? response.body().getMessage() : "Server error"));
                }
            }

            @Override
            public void onFailure(Call<DeleteExpResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
            }
        });
    }

}