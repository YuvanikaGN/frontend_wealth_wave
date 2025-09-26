package com.simats.wealth_wave.models;

public class ChatMessage {
    private String message;
    private boolean isUser;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    private boolean isTyping = false;

    public ChatMessage(boolean isTyping) { this.isTyping = isTyping; }
    public boolean isTyping() { return isTyping; }

}
