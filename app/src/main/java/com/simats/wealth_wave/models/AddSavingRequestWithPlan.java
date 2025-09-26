package com.simats.wealth_wave.models;

public class AddSavingRequestWithPlan {
    private String user_id;
    private String amount;
    private String plan_id;

    public AddSavingRequestWithPlan(String user_id, String amount, String plan_id) {
        this.user_id = user_id;
        this.amount = amount;
        this.plan_id = plan_id;
    }

    public String getUser_id() { return user_id; }
    public String getAmount() { return amount; }
    public String getPlan_id() { return plan_id; }
}


