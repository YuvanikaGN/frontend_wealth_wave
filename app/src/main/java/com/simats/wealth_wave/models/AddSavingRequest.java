package com.simats.wealth_wave.models;

public class AddSavingRequest {
    private int user_id;
    private int plan_id;
    private double amount;

    public AddSavingRequest(int user_id, int plan_id, double amount) {
        this.user_id = user_id;
        this.plan_id = plan_id;
        this.amount = amount;
    }

    // Optional: getters and setters
}
