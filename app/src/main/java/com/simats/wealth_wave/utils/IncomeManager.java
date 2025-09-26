package com.simats.wealth_wave.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class IncomeManager {

    private static final String PREF_NAME = "WealthWavePrefs";
    private static final String KEY_INCOME = "monthly_income";
    private static IncomeManager instance;
    private final SharedPreferences prefs;

    private IncomeManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized IncomeManager getInstance(Context context) {
        if (instance == null) {
            instance = new IncomeManager(context.getApplicationContext());
        }
        return instance;
    }

//    public static void saveIncome(float income) {
//        prefs.edit().putFloat(KEY_INCOME, income).apply();
//    }

    public float getIncome() {
        return prefs.getFloat(KEY_INCOME, 0.0f);
    }

    public void clearIncome() {
        prefs.edit().remove(KEY_INCOME).apply();
    }
}
