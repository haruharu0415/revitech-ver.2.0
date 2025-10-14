package com.example.revitech.entity;

import java.time.LocalDateTime;

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
    
    // 【必須】ルームID
    private Long roomId; 
    
    // 【必須】送信者ID (クライアントJSと合わせる)
    private Long senderUserId; 
    
    // メッセージ内容 (列名エラーの原因となったフィールド)
    private String content;
    
    // 作成日時 (DB自動生成)
    private LocalDateTime createdAt; 
    
    // デフォルトコンストラクタ (JPA/Jacksonで必須)
    public ChatMessage() {} 
    
    // コンストラクタ (ChatServiceで利用)
    public ChatMessage(Long roomId, Long senderUserId, String content) {
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.content = content;
        // createdAt は PrePersist で設定するためここでは設定しない
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // idのsetterを追加（Jacksonのデシリアライズ用）
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 

}