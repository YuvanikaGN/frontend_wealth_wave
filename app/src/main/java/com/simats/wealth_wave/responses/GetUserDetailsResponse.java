package com.simats.wealth_wave.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetUserDetailsResponse {
    private boolean status;

    @SerializedName("active_goals")
    private int activeGoals;

    @SerializedName("target_amount")
    private float targetAmount;

    @SerializedName("timeline_months")
    private int timelineMonths;

    @SerializedName("monthly_target")
    private float monthlyTarget;

    private List<Goal> goals;

    public boolean isStatus() { return status; }
    public int getActiveGoals() { return activeGoals; }
    public float getTargetAmount() { return targetAmount; }
    public int getTimelineMonths() { return timelineMonths; }
    public int getMonthlyTarget() { return (int) monthlyTarget; }
    public List<Goal> getGoals() { return goals; }

    public static class Goal {
        private String goal;
        private float target_amount;
        private String duration;
        private float income;
        private float approx_money;
        private String created_at;

        public String getGoal() { return goal; }
        public float getTarget_amount() { return target_amount; }
        public String getDuration() { return duration; }
        public float getIncome() { return income; }
        public float getApprox_money() { return approx_money; }
        public String getCreated_at() { return created_at; }
    }
}
