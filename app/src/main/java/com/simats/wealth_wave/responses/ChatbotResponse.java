package com.simats.wealth_wave.responses;

public class ChatbotResponse {
    private boolean success;
    private String reply;
    private String error;      // optional: PHP includes this on failure
    private String response;   // optional raw response

    public boolean isSuccess() { return success; }
    public String getReply() { return reply; }
    public String getError() { return error; }
    public String getResponse() { return response; }
}
