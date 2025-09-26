package com.simats.wealth_wave.models;

public class Expense {
    private int id;
    private int user_id;
    private float amount;
    private String category;
    private String mode;
    private String note;
    private String created_at;

    // Getters
    public int getId() { return id; }
    public int getUserId() { return user_id; }
    public float getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getMode() { return mode; }
    public String getNote() { return note; }
    public String getCreatedAt() { return created_at; }
}
