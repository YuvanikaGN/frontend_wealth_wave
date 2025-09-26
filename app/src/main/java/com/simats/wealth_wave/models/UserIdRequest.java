package com.simats.wealth_wave.models;

public class UserIdRequest {
    private int user_id;

    public UserIdRequest(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
