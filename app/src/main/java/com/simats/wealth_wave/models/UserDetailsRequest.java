package com.simats.wealth_wave.models;


public class UserDetailsRequest {
    private String goal;
    private double target_amount;
    private double income;
    private String duration;
    private int user_id; // add this
    private String created_at; // add this

    public UserDetailsRequest(String goal, double target_amount, double income, String duration, int user_id) {
        this.goal = goal;
        this.target_amount = target_amount;
        this.income = income;
        this.duration = duration;
        this.user_id = user_id;
    }

    // getters and setters (if needed)
}
