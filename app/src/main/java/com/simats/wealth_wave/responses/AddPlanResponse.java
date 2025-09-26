package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;

public class AddPlanResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("approx_money")
    private double approxMoney;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public double getApproxMoney() {
        return approxMoney;
    }
}
