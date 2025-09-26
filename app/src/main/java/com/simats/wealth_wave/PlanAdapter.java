package com.simats.wealth_wave;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.wealth_wave.responses.GetAllPlansResponse;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<GetAllPlansResponse.Data> plans;

    public PlanAdapter(List<GetAllPlansResponse.Data> plans) {
        this.plans = plans;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_card, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        GetAllPlansResponse.Data plan = plans.get(position);

        holder.tvGoalPlan.setText(plan.getGoal());
        holder.tvTargetAmount.setText("Target: ₹ " + plan.getTarget_amount());
        holder.tvDuration.setText("Duration: " + plan.getDuration());
        holder.tvIncome.setText("Income: ₹ " + plan.getIncome());
        holder.tvApproxMoney.setText("Approx Money: ₹ " + plan.getApprox_money());
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalPlan, tvTargetAmount, tvDuration, tvIncome, tvApproxMoney;
        ImageView ivArrow;
        LinearLayout detailsLayout;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalPlan = itemView.findViewById(R.id.tvGoalPlan);
            tvTargetAmount = itemView.findViewById(R.id.tvTargetAmount);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvIncome = itemView.findViewById(R.id.tvIncome);
            tvApproxMoney = itemView.findViewById(R.id.tvApproxMoney);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);

            // Toggle on arrow click with animation
            ivArrow.setOnClickListener(v -> {
                if (detailsLayout.getVisibility() == View.GONE) {
                    expand(detailsLayout);
                    ivArrow.animate().rotation(90f).setDuration(200).start(); // rotate down
                } else {
                    collapse(detailsLayout);
                    ivArrow.animate().rotation(0f).setDuration(200).start(); // rotate back
                }
            });
        }
    }

    // ----------- Expand & Collapse Animations ----------- //
    private void expand(View view) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        animator.setDuration(300);
        animator.start();
    }

    private void collapse(View view) {
        int initialHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        animator.setDuration(300);
        animator.start();
    }
}
