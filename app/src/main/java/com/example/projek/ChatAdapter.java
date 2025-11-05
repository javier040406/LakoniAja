package com.example.projek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (chatMessage.isUser()) {
            holder.layoutUser.setVisibility(View.VISIBLE);
            holder.layoutBot.setVisibility(View.GONE);
            holder.textUserMessage.setText(chatMessage.getMessage());
        } else {
            holder.layoutUser.setVisibility(View.GONE);
            holder.layoutBot.setVisibility(View.VISIBLE);
            holder.textBotMessage.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void updateData(List<ChatMessage> newMessages) {
        this.chatMessages = newMessages;
        notifyDataSetChanged();
    }
    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1);
    }
    public void clearMessages() {
        chatMessages.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View layoutUser;
        public View layoutBot;
        public TextView textUserMessage;
        public TextView textBotMessage;
        public ImageView imageBot;

        public ViewHolder(View view) {
            super(view);
            layoutUser = view.findViewById(R.id.layoutUser);
            layoutBot = view.findViewById(R.id.layoutBot);
            textUserMessage = view.findViewById(R.id.textUserMessage);
            textBotMessage = view.findViewById(R.id.textBotMessage);
            imageBot = view.findViewById(R.id.imageBot);
        }
    }
}