package com.example.revitech.dto;

import java.time.LocalDateTime;

import com.example.revitech.entity.ChatMessage;

public class ChatMessageDto {
    private Integer messageId;
    private Integer roomId;
    private Integer userId;
    private String userName; 
    private String content;  
    private LocalDateTime createdAt;

    // デフォルトコンストラクタ（念のため）
    public ChatMessageDto() {}

    // エンティティから変換するコンストラクタ
    public ChatMessageDto(ChatMessage entity, String senderName) {
        this.messageId = entity.getMessageId();
        this.roomId = entity.getRoomId();
        this.userId = entity.getUserId();
        this.content = entity.getContent();
        this.createdAt = entity.getCreatedAt();
        this.userName = senderName;
    }

    // --- 以下、明示的なゲッター・セッター ---

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}