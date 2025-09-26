package com.simats.wealth_wave.models;

public class TransactionRequest {
    int user_id;
    double amount;
    String category;
    String mode;
    String note;
    int is_income;

    public TransactionRequest(int user_id, double amount, String category, String mode, String note, int is_income) {
        this.user_id = user_id;
        this.amount = amount;
        this.category = category;
        this.mode = mode;
        this.note = note;
        this.is_income = is_income;
    }
}

