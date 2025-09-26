package com.simats.wealth_wave.responses;
public class UpdateSavingsPlanResponse {
    private boolean status;
    private String message;
    private double approx_money;
    private String duration;

    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public double getApproxMoney() { return approx_money; }
    public String getDuration() { return duration; }
}

