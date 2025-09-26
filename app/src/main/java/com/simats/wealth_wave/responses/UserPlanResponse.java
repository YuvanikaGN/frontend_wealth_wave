package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;

public class UserPlanResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("target_amount")
    private double targetAmount;

    @SerializedName("approx_money")  // matches your PHP response
    private double approxMoney;

    @SerializedName("duration")
    private String duration;

    @SerializedName("message")
    private String message;

    // Optional fields for compatibility (if some APIs still return these)
    @SerializedName("monthly_target")
    private Double monthlyTarget; // nullable

    @SerializedName("timeline_months")
    private Integer timelineMonths; // nullable

    // ---------------- Getters ----------------
    public boolean isStatus() {
        return status;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getApproxMoney() {
        return approxMoney;
    }

    public String getDuration() {
        return duration;
    }

    public String getMessage() {
        return message;
    }

    public Double getMonthlyTarget() {
        return monthlyTarget;
    }

    public Integer getTimelineMonths() {
        return timelineMonths;
    }
}
