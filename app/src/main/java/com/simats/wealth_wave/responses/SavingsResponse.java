package com.simats.wealth_wave.responses;

import java.util.List;

public class SavingsResponse {
    public boolean ok;
    private double total_savings; // total savings
    public List<Saving> data;

    public static class Saving {
        public String id;
        public String user_id;
        public String amount;
    }

    public double getTotal_savings() {
        return total_savings;
    }
}
