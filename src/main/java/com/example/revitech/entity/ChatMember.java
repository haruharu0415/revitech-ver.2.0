package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_members")
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 【最重要】DBの列名 'room_id' にマッピング
    @Column(name = "room_id")
    private Long roomId;

    // 【最重要】DBの列名 'user_id' にマッピング
    @Column(name = "user_id")
    private Long userId;

    // デフォルトコンストラクタ (JPAで必須)
    public ChatMember() {}

    // コンストラクタ (Service層で利用)
    public ChatMember(Long roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}