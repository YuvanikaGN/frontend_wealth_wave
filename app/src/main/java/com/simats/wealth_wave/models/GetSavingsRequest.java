package com.simats.wealth_wave.models;

public class GetSavingsRequest {
    private int user_id;
    private int plan_id;

    public GetSavingsRequest(int user_id, int plan_id) {
        this.user_id = user_id;
        this.plan_id = plan_id;
    }

    public int getUser_id() { return user_id; }
    public int getPlan_id() { return plan_id; }
}
