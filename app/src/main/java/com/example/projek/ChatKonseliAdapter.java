package com.example.projek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatKonseliAdapter extends RecyclerView.Adapter<ChatKonseliAdapter.ViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatKonseliAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_konseli, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        // PENYESUAIAN: isUser = true → KONSELI (kanan), isUser = false → KONSELOR (kiri)
        if (chatMessage.isUser()) {
            holder.layoutKonseli.setVisibility(View.VISIBLE);
            holder.layoutKonselor.setVisibility(View.GONE);
            holder.textKonseliMessage.setText(chatMessage.getMessage());
        } else {
            holder.layoutKonseli.setVisibility(View.GONE);
            holder.layoutKonselor.setVisibility(View.VISIBLE);
            holder.textKonselorMessage.setText(chatMessage.getMessage());
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
        public LinearLayout layoutKonseli;
        public LinearLayout layoutKonselor;
        public TextView textKonseliMessage;
        public TextView textKonselorMessage;

        public ViewHolder(View view) {
            super(view);
            layoutKonseli = view.findViewById(R.id.layoutKonseli);
            layoutKonselor = view.findViewById(R.id.layoutKonselor);
            textKonseliMessage = view.findViewById(R.id.textKonseliMessage);
            textKonselorMessage = view.findViewById(R.id.textKonselorMessage);
        }
    }
}