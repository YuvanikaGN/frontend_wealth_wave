package com.simats.wealth_wave.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("fullName") // should match your JSON from backend
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("income")
    private double income;

    @SerializedName("token") // should match JSON key from backend
    private String token;

    // Empty constructor (needed for Gson)
    public User() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
