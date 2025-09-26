package com.simats.wealth_wave.models;
import com.google.gson.annotations.SerializedName;

public class SignupRequest {
    private String fullName;
    private String email;
    private String password;
    private String mobile;

    public SignupRequest(String fullName, String email, String password, String mobile) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
    }

    // Getters & Setters
}

