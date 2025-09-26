package com.simats.wealth_wave.responses;

public class GetSavingsPlanResponse {
    private int id; // add this

    private boolean status;
    private Data data;

    private String message;

    public int getId() { return id; }
    public boolean isStatus() { return status; }
    public Data getData() { return data; }
    public String getMessage() { return message; }

    public static class Data {
        private int id; // <-- Add this
        private String goal;
        private double target_amount;
        private double income;
        private String duration;

        public String getGoal() { return goal; }
        public double getTarget_amount() { return target_amount; }
        public double getIncome() { return income; }
        public String getDuration() { return duration; }
        public int getId() { return id; } // <-- Add this
    }
}
