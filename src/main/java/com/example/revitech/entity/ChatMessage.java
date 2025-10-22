package com.example.revitech.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "users_id", nullable = false) // DB列名 users_id
    private Long senderUserId; // Java変数名

    @Lob
    @Column(nullable = false)
    private String body;

    // ★ フィールド名を createdAt (キャメルケース) に変更 ★
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false) // DB列名は created_at
    private LocalDateTime createdAt; // ← ここを変更

    public ChatMessage() {}

    public ChatMessage(Long roomId, Long senderUserId, String body) {
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.body = body;
        // createdAt は @CreationTimestamp で自動設定
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    // ★ Getter/Setter 名も createdAt に変更 ★
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    // --- End of Getters and Setters ---
}