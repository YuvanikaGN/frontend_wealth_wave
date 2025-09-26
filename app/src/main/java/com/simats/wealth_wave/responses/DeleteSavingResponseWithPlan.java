package com.simats.wealth_wave.responses;

public class DeleteSavingResponseWithPlan {
    private boolean ok;
    private String message;
    private int deleted_id;
    private int plan_id;

    public boolean isOk() { return ok; }
    public String getMessage() { return message; }
    public int getDeleted_id() { return deleted_id; }
    public int getPlan_id() { return plan_id; }
}
