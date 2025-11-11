package com.example.revitech.dto;

import java.time.LocalDateTime;

// import java.util.UUID; // ★ UUID は使わない
import com.example.revitech.entity.ChatMessage; // ★ ChatMessage の senderUserId は Long

public class ChatMessageDto {

    private Long roomId;
    private Long senderUserId; // ★ 型を Long に戻す
    private String senderName;
    private String content;
    private LocalDateTime createdAt;

    public ChatMessageDto(ChatMessage message, String senderName) {
        this.roomId = message.getRoomId();
        this.senderUserId = message.getSenderUserId(); // ★ message.getSenderUserId() は Long を返す
        this.senderName = senderName;
        this.content = message.getBody(); // ★ ChatMessage の getBody()
        this.createdAt = message.getCreatedAt(); // ★ ChatMessage の getCreatedAt()
    }

    public ChatMessageDto() {}

    // Getters and Setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getSenderUserId() { return senderUserId; } // ★ 戻り値の型を Long に
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; } // ★ 引数の型を Long に

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}