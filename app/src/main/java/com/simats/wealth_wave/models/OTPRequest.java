package com.simats.wealth_wave.models;

public class OTPRequest {
    private String email;

    public OTPRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

