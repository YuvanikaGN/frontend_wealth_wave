package com.simats.wealth_wave.models;

public class DeleteExpRequest {

    private int user_id;
    private int id;

    public DeleteExpRequest(int user_id, int id) {
        this.user_id = user_id;
        this.id = id;
    }
}
