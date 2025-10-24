package com.example.revitech.entity;

import java.io.Serializable;
import java.util.Objects;
// import java.util.UUID; // ★ UUID は使わない

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ChatMemberId implements Serializable {

    @Column(name = "room_id")
    private Long roomId; // ★ 型を Long に (chat_rooms.room_id と同じ)

    @Column(name = "users_id")
    private Long userId; // ★ 型を Long に (Users.users_id と同じ)

    // --- コンストラクタ ---
    public ChatMemberId() {}

    public ChatMemberId(Long roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    // --- Getters and Setters ---
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // --- equals() と hashCode() (複合主キーには必須) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMemberId that = (ChatMemberId) o;
        return Objects.equals(roomId, that.roomId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, userId);
    }
}