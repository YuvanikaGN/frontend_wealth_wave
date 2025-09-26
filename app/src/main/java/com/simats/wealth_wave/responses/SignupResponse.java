package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;


public class SignupResponse {
    private String status;
    private String message;

    @SerializedName("id")  // maps "id" in JSON to userId field
    private int userId;

    // getters and setters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public int getUserId() { return userId; }

    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setUserId(int userId) { this.userId = userId; }
}
