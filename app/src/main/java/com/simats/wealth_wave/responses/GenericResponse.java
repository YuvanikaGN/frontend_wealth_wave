package com.simats.wealth_wave.responses;

public class GenericResponse {
    private String status;

    private boolean statusbool;
    private String message;
    private boolean success;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isStatus() {
        return statusbool;
    }
}
