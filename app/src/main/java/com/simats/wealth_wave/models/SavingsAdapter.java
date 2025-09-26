package com.simats.wealth_wave.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.wealth_wave.R;

import java.util.List;

public class SavingsAdapter extends RecyclerView.Adapter<SavingsAdapter.ViewHolder> {

    private List<SavingItem> savingsList;
    private OnItemLongClickListener longClickListener;

    public SavingsAdapter(List<SavingItem> savingsList) {
        this.savingsList = savingsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saving, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavingItem item = savingsList.get(position);
        holder.tvSavedAmount.setText("₹ " + String.format("%.2f", item.getAmount()));
        holder.tvSavedDateTime.setText(item.getDate());

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(holder.getAdapterPosition(), savingsList.get(holder.getAdapterPosition()));
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return savingsList.size();
    }

    public void setSavingsList(List<SavingItem> list) {
        this.savingsList = list;
    }


    // Interface for long clicks
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, SavingItem item);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSavedAmount, tvSavedDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSavedAmount = itemView.findViewById(R.id.tvSavedAmount);
            tvSavedDateTime = itemView.findViewById(R.id.tvSavedDateTime);
        }
    }
}
