package com.simats.wealth_wave.models;

public class SessionManager {
    private static SessionManager instance;
    private int userId = -1;

    private SessionManager() { }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUserId(int id) { userId = id; }
    public int getUserId() { return userId; }
    public boolean isLoggedIn() { return userId != -1; }

    public void clearSession() {
    }
}

