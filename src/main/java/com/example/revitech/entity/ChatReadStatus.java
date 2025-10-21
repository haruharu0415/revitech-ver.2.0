package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "chat_read_status",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "room_id"})) // 複合ユニーク制約
public class ChatReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    public ChatReadStatus() {} // JPAのための空のコンストラクタ

    public ChatReadStatus(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
        this.lastReadAt = LocalDateTime.now(); // 作成時に現在時刻を設定
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }
    // --- End of Getters and Setters ---
}