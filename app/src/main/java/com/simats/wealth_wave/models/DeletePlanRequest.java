package com.simats.wealth_wave.models;

import com.google.gson.annotations.SerializedName;

public class DeletePlanRequest {
    @SerializedName("plan_id")
    private int planId;

    public DeletePlanRequest(int planId) {
        this.planId = planId;
    }

    public int getPlanId() {
        return planId;
    }
}
