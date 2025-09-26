package com.simats.wealth_wave.models;


public class UpdateProfileRequest {
    private String email;
    private String fullName;
    private String mobile;

    public UpdateProfileRequest(String email, String fullName, String mobile) {
        this.email = email;
        this.fullName = fullName;
        this.mobile = mobile;
    }

    // Add getters/setters if needed
}



