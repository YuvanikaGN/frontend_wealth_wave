package com.simats.wealth_wave.responses;

import com.simats.wealth_wave.models.Plan;

import java.util.List;

public class PlanResponse {
    private boolean status;
    private String message;
    private List<Plan> data;

    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public List<Plan> getData() { return data; }
}
