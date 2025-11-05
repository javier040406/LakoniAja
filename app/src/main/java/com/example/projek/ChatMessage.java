package com.example.projek;
public class ChatMessage {
    private String message;
    private boolean isUser;
    private long timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }
    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setUser(boolean user) {
        isUser = user;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getFormattedTime() {
        return android.text.format.DateFormat.format("HH:mm", timestamp).toString();
    }
}