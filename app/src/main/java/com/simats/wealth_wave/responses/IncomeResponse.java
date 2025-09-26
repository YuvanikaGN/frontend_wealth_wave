package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;

public class IncomeResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("income")
    private String income;

    @SerializedName("approx_money")
    private String approxMoney; // new field
    @SerializedName("target_amount")
    private String targetAmount; // new field

    public boolean isStatus() {
        return status;
    }

    public String getIncome() {
        return income;
    }

    public String getApproxMoney() {
        return approxMoney;
    }

    public String getTargetAmount() {
        return targetAmount;
    }
}
