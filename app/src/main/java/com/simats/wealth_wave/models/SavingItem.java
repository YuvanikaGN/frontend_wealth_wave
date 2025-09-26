package com.simats.wealth_wave.models;

public class SavingItem {
    private int id;
    private double amount;
    private String date;

    public SavingItem(int id, double amount, String date) {
        this.id = id;
        this.amount = amount;
        this.date = date;
    }

    // Fallback constructor if you don’t have backend id
    public SavingItem(double amount, String date) {
        this.id = -1; // mark as local-only
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
}
