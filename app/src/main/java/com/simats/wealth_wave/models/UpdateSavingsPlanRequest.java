package com.simats.wealth_wave.models;
public class UpdateSavingsPlanRequest {
    private int plan_id; // match PHP
    private String goal;
    private double target_amount;
    private double income;
    private String duration; // yyyy-mm-dd

    public UpdateSavingsPlanRequest(int plan_id, String goal, double target_amount, double income, String duration) {
        this.plan_id = plan_id;
        this.goal = goal;
        this.target_amount = target_amount;
        this.income = income;
        this.duration = duration;
    }
}
