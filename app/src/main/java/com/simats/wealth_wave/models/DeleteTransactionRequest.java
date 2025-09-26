package com.simats.wealth_wave.models;

public class DeleteTransactionRequest {
    private int user_id;
    private int id;

    public DeleteTransactionRequest(int user_id, int id) {
        this.user_id = user_id;
        this.id = id;
    }
}
