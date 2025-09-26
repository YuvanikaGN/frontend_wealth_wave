package com.simats.wealth_wave.models;

import com.google.gson.annotations.SerializedName;

public class UpdateIncomeRequest {
    private int id;
    private double income;

    public UpdateIncomeRequest(int id, double income) {
        this.id = id;
        this.income = income;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }
}





