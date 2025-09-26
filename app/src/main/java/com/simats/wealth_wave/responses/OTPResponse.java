package com.simats.wealth_wave.responses;

public class OTPResponse {
    private boolean status;
    private String message;
    private String otp;

    // Getter methods
    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getOtp() {
        return otp;
    }
}

