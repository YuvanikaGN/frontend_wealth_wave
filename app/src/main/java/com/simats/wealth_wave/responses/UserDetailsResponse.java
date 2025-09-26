package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;

public class UserDetailsResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("approx_money")
    private double approxMoney;
    @SerializedName("created_at")
    private String createdAt;   // ✅ Add this

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }
    public double getApproxMoney() {
        return approxMoney;
    }
    public String getCreatedAt() {
        return createdAt;
    }
}
