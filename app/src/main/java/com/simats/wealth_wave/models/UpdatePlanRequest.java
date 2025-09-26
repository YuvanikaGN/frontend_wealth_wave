package com.simats.wealth_wave.models;


import com.google.gson.annotations.SerializedName;

public class UpdatePlanRequest {

    @SerializedName("id")
    private int id;

    @SerializedName("goal")
    private String goal;

    @SerializedName("target_amount")
    private double targetAmount;

    @SerializedName("income")
    private double income;

    @SerializedName("duration")
    private String duration;  // format: dd/mm/yyyy

    public UpdatePlanRequest(int id, String goal, double targetAmount, double income, String duration) {
        this.id = id;
        this.goal = goal;
        this.targetAmount = targetAmount;
        this.income = income;
        this.duration = duration;
    }

    // getters and setters if needed
}
