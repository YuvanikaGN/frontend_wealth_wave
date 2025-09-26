package com.simats.wealth_wave.responses;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetSavingsResponse {

    @SerializedName("ok")
    private boolean ok;

    @SerializedName("total_savings")
    private double totalSavings;

    @SerializedName("data")
    private List<SavingData> data;

    public boolean isOk() { return ok; }
    public double getTotalSavings() { return totalSavings; }
    public List<SavingData> getData() { return data; }

    public static class SavingData {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("plan_id")
        private int planId;
        @SerializedName("amount")
        private double amount;
        @SerializedName("date_time")
        private String dateTime;
        @SerializedName("created_at")
        private String createdAt;

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getPlanId() { return planId; }
        public double getAmount() { return amount; }
        public String getDateTime() { return dateTime; }
        public String getCreatedAt() { return createdAt; }
    }
}

