package com.simats.wealth_wave.responses;

public class DeleteExpResponse {
    private boolean ok;
    private int deleted_id;
    private String message;
    private String error;

    public boolean isOk() { return ok; }
    public int getDeletedId() { return deleted_id; }
    public String getMessage() { return message; }
    public String getError() { return error; }
}
