package com.simats.wealth_wave.models;

import com.google.gson.annotations.SerializedName;

public class AddPlanRequest {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("goal")
    private String goal;

    @SerializedName("target_amount")
    private double targetAmount;

    @SerializedName("income")
    private double income;

    @SerializedName("duration")
    private String duration; // changed from int to String

    public AddPlanRequest(int userId, String goal, double targetAmount, double income, String duration) {
        this.userId = userId;
        this.goal = goal;
        this.targetAmount = targetAmount;
        this.income = income;
        this.duration = duration;
    }
}
