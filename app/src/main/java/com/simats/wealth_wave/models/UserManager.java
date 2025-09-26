package com.simats.wealth_wave.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class UserManager {
    private static final String PREFS = "user_prefs";
    private static final String KEY_USER = "key_user_json";

    private static UserManager instance;
    private SharedPreferences prefs;
    private Gson gson;

    private UserManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized UserManager getInstance(Context ctx) {
        if (instance == null) instance = new UserManager(ctx);
        return instance;
    }

    public void saveUser(User user) {
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply();
    }

    public User getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return null;
        return gson.fromJson(json, User.class);
    }

    public String getToken() {
        User u = getUser();
        return u == null ? null : u.getToken();
    }

    public void updateUsernameLocally(String newName) {
        User u = getUser();
        if (u != null) {
            u.setFullName(newName);
            saveUser(u);
        }
    }

    public void updateIncomeLocally(double newIncome) {
        User u = getUser();
        if (u != null) {
            u.setIncome(newIncome);
            saveUser(u);
        }
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}

