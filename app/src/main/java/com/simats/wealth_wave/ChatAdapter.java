package com.simats.wealth_wave;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.wealth_wave.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private static final int VIEW_TYPE_TYPING = 3;

    private final List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = chatMessages.get(position);
        if (msg.isTyping()) return VIEW_TYPE_TYPING;
        return msg.isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_USER:
                return new UserViewHolder(inflater.inflate(R.layout.item_message_user, parent, false));
            case VIEW_TYPE_BOT:
                return new BotViewHolder(inflater.inflate(R.layout.item_message_bot, parent, false));
            case VIEW_TYPE_TYPING:
            default:
                return new TypingViewHolder(inflater.inflate(R.layout.item_message_typing, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).textMessage.setText(message.getMessage());
        } else if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).textMessage.setText(message.getMessage());
        } else if (holder instanceof TypingViewHolder) {
            TypingViewHolder vh = (TypingViewHolder) holder;

            // Load bounce animation
            Animation bounce1 = AnimationUtils.loadAnimation(vh.itemView.getContext(), R.anim.bounce_dot);
            Animation bounce2 = AnimationUtils.loadAnimation(vh.itemView.getContext(), R.anim.bounce_dot);
            Animation bounce3 = AnimationUtils.loadAnimation(vh.itemView.getContext(), R.anim.bounce_dot);

            // Staggered start offsets
            bounce2.setStartOffset(200);
            bounce3.setStartOffset(400);

            vh.dot1.startAnimation(bounce1);
            vh.dot2.startAnimation(bounce2);
            vh.dot3.startAnimation(bounce3);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // ===== ViewHolders =====
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        UserViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        BotViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        View dot1, dot2, dot3;

        TypingViewHolder(View itemView) {
            super(itemView);
            dot1 = itemView.findViewById(R.id.dot1);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
        }
    }
}
