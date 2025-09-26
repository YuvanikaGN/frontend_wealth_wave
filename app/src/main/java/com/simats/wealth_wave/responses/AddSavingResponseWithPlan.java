package com.simats.wealth_wave.responses;

public class AddSavingResponseWithPlan {
    private boolean ok;
    private String message;
    private Data data;

    public boolean isOk() { return ok; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public int getSavingId() {
        return data != null ? data.getId() : 0;
    }

    public class Data {
        private int id;
        private int user_id;
        private double amount;
        private int plan_id;
        private String inserted_at;

        public int getId() { return id; }
        public int getUser_id() { return user_id; }
        public double getAmount() { return amount; }
        public int getPlan_id() { return plan_id; }
        public String getInserted_at() { return inserted_at; }
    }
}
