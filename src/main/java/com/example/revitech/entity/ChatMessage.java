package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column; // 【重要】追加
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
    
    // 【必須】ルームID (デフォルトで 'room_id' にマッピングされる想定)
    private Long roomId; 
    
    // 【必須】送信者ID (デフォルトで 'sender_user_id' にマッピングされる想定)
    private Long senderUserId; 
    
    // メッセージ内容 
    // 【重要修正】DBの実際のカラム名 'body' にマッピング
    @Column(name = "body", nullable = false) 
    private String content;
    
    // 作成日時 
    // 【重要修正】DBの実際のカラム名 'created_at' にマッピング
    @Column(name = "created_at", nullable = false) 
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
        // DB自動生成ではなく、Java側で時刻を設定
        this.createdAt = LocalDateTime.now(); 
    }

    // Getters and Setters
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