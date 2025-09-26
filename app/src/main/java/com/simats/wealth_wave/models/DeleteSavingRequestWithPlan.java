package com.simats.wealth_wave.models;

public class DeleteSavingRequestWithPlan {
    private int id;
    private int plan_id;

    public DeleteSavingRequestWithPlan(int id, int plan_id) {
        this.id = id;
        this.plan_id = plan_id;
    }

    public int getId() { return id; }
    public int getPlan_id() { return plan_id; }
}
