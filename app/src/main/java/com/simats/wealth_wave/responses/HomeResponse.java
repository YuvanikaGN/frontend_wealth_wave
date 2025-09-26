package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HomeResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("fullName") // if backend sends fullName
    private String fullName;

    @SerializedName("income")
    private String income;

    @SerializedName("targetAmount")
    private String targetAmount;

    @SerializedName("goal")
    private String goal;

    @SerializedName("duration")
    private String duration;

    @SerializedName("savedAmount") // ensure matches backend JSON
    private int savedAmount;

    @SerializedName("progress")
    private int progress;

    @SerializedName("monthsRemaining")
    private int monthsRemaining;

    @SerializedName("approxMoney")
    private int approxMoney;

    @SerializedName("message")
    private String message;

    @SerializedName("savingsHistory")
    private List<Saving> savingsHistory;

    // ✅ Getters
    public boolean isSuccess() { return success; }
    public String getFullName() { return fullName; }
    public String getIncome() { return income; }
    public String getTargetAmount() { return targetAmount; }
    public String getGoal() { return goal; }
    public String getDuration() { return duration; }
    public int getSavedAmount() { return savedAmount; }
    public int getProgress() { return progress; }
    public int getMonthsRemaining() { return monthsRemaining; }
    public int getApproxMoney() { return approxMoney; }
    public String getMessage() { return message; }
    public List<Saving> getSavingsHistory() { return savingsHistory; }

    // ✅ Inner class for individual savings
    public static class Saving {

        @SerializedName("id")
        private String id;

        @SerializedName("amount")
        private String amount;

        @SerializedName("inserted_at")
        private String insertedAt;

        // ✅ Getters
        public String getId() { return id; }
        public String getAmount() { return amount; }
        public String getInsertedAt() { return insertedAt; }
    }
}
