package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long roomId; 
    
    // ★★★ データベースのカラム名に合わせる ★★★
    @Column(name = "sender_user_id")
    private Long senderUserId; 
    
    @Column(name = "body", nullable = false) 
    private String content;
    
    @Column(name = "created_at", nullable = false) 
    private LocalDateTime createdAt; 
    
    // JPA/Jacksonのためのデフォルトコンストラクタ
    public ChatMessage() {} 
    
    // サービス層で使うコンストラクタ
    public ChatMessage(Long roomId, Long senderUserId, String content) {
        this.roomId = roomId;
        this.senderUserId = senderUserId; // ★ senderUserId を受け取る
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); 
    }

    // --- Getter and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 
}