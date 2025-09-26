package com.simats.wealth_wave.models;

public class ChatbotRequest {
    private String message;
    public ChatbotRequest(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
