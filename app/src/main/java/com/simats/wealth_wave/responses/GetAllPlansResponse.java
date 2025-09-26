package com.simats.wealth_wave.responses;

import java.util.List;

public class GetAllPlansResponse {
    private boolean status;
    private String message;
    private List<Data> data;
    private int id; // database primary key
    public int getId() { return id; }


    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public List<Data> getData() { return data; }

    public static class Data {
        private int id;
        private String goal;
        private double target_amount;
        private double income;
        private String duration;
        private Double approx_money;

        public int getId() { return id; }
        public String getGoal() { return goal; }
        public double getTarget_amount() { return target_amount; }
        public double getIncome() { return income; }
        public String getDuration() { return duration; }
        public Double getApprox_money() {
            return approx_money != null ? approx_money : 0.0;
        }

    }

}
