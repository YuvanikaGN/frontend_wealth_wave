package com.simats.wealth_wave.responses;

public class DeleteResponse {
    private boolean success;   // NOT "status" or anything else
    private String message;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}


