package com.simats.wealth_wave.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.wealth_wave.R;
import com.simats.wealth_wave.responses.GetAllPlansResponse;

import java.util.List;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlanViewHolder> {
    private List<GetAllPlansResponse.Data> plansList;

    public PlansAdapter(List<GetAllPlansResponse.Data> plansList) {
        this.plansList = plansList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        GetAllPlansResponse.Data plan = plansList.get(position);
        holder.tvGoal.setText("Goal: " + plan.getGoal());
        holder.tvTarget.setText("Target: ₹" + plan.getTarget_amount());
        holder.tvIncome.setText("Income: ₹" + plan.getIncome());
        holder.tvDuration.setText("Duration: " + plan.getDuration());
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoal, tvTarget, tvIncome, tvDuration;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoal = itemView.findViewById(R.id.tvGoal);
            tvTarget = itemView.findViewById(R.id.tvTargetAmount);
            tvIncome = itemView.findViewById(R.id.tvIncome);
//            tvDuration = itemView.findViewById(R.id.tvDuration);
        }
    }
}
