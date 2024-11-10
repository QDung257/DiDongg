package com.example.project183.Admin;

public class ChatMessage {
    private String messageId;
    private String senderId;
    private String content;
    private long timestamp;
    private boolean isAdmin;


    public ChatMessage() {
    }

    public ChatMessage(String messageId, String senderId, String content, boolean isAdmin) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.isAdmin = isAdmin;
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
