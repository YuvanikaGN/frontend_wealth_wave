package com.simats.wealth_wave.responses;

import java.util.Map;

public class BankVerifyResponse {
    public static boolean BankData;
    private String status;
    private String message;
    private Map<String, String> data; // flexible map to hold bank details

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getData() {
        return data;
    }
}
