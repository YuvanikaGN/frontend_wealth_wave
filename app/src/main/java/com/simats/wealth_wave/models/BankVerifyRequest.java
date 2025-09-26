package com.simats.wealth_wave.models;

public class BankVerifyRequest {
    private String acc_number;
    private String ifsc_code;

    public BankVerifyRequest(String acc_number, String ifsc_code) {
        this.acc_number = acc_number;
        this.ifsc_code = ifsc_code;
    }

    public String getAcc_number() {
        return acc_number;
    }

    public String getIfsc_code() {
        return ifsc_code;
    }
}
