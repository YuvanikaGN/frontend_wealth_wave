package com.simats.wealth_wave.models;

public class Plan {
    private int id;
    private String goal;
    private String target_amount;
    private String income;
    private String duration;
    private String created_at;

    // Getters
    public int getId() { return id; }
    public String getGoal() { return goal; }
    public String getTarget_amount() { return target_amount; }
    public String getDuration() { return duration; }
    public String getCreated_at() { return created_at; }

    public String getIncome() {
        return income;
    }
}

