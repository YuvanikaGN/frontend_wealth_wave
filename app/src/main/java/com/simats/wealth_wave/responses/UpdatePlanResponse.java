package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;

public class UpdatePlanResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
