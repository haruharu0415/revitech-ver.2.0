package com.example.revitech.entity;

import java.time.LocalDateTime;
// import java.util.UUID; // ★ UUID は使わない

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "chat_read_status",
       uniqueConstraints = @UniqueConstraint(columnNames = {"users_id", "room_id"})) // 複合UNIQUE制約
public class ChatReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ★ DBの id (BIGINT) に対応

    @Column(name = "users_id", nullable = false)
    private Long userId; // ★ 型を Long に

    @Column(name = "room_id", nullable = false)
    private Long roomId; // ★ 型を Long に (chat_rooms.room_id)

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    // Usersエンティティへの関連付け (任意)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", insertable = false, updatable = false)
    private Users user;

    // ChatRoomエンティティへの関連付け (任意)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private ChatRoom room;

    // --- コンストラクタ ---
    public ChatReadStatus() {}

    public ChatReadStatus(Long userId, Long roomId, LocalDateTime lastReadAt) {
        this.userId = userId;
        this.roomId = roomId;
        this.lastReadAt = lastReadAt;
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

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public ChatRoom getRoom() { return room; }
    public void setRoom(ChatRoom room) { this.room = room; }
}