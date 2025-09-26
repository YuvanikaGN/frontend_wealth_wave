package com.simats.wealth_wave.models;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "WealthWavePrefs";
    private static final String KEY_INCOME = "monthly_income";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveIncome(float income) {
        editor.putFloat(KEY_INCOME, income);
        editor.apply();
    }

    public float getIncome() {
        return prefs.getFloat(KEY_INCOME, 0f); // default 0 if not set
    }
}
