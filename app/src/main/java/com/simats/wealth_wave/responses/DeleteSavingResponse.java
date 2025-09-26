package com.simats.wealth_wave.responses;

public class DeleteSavingResponse {
    private int id;
    private boolean ok;
    private String message;
    private int deleted_id;

    // Use the actual backend value
    public boolean isOk() {
        return ok;
    }

    // Optional: getters
    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getDeletedId() {
        return deleted_id;
    }
}
