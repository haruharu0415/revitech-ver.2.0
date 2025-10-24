package com.example.revitech.entity;

import java.time.LocalDateTime;
// import java.util.UUID; // ★ UUID は使わない

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId; // メッセージID (PK)

    @Column(name = "room_id", nullable = false)
    private Long roomId; // ルームID (FK)

    // ★ DB の users_id 列 (送信者ID) に対応 ★
    @Column(name = "users_id", nullable = false)
    private Long senderUserId; // ★ 型を Long に

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body; // メッセージ本文

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- コンストラクタ ---
    public ChatMessage() {}

    public ChatMessage(Long roomId, Long senderUserId, String body) {
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.body = body;
    }

    // --- Getters and Setters ---
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}