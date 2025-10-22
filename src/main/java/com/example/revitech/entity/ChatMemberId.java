package com.example.revitech.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable // 複合主キークラスを示す
public class ChatMemberId implements Serializable {

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "users_id") // DB定義に合わせる
    private Long userId;

    public ChatMemberId() {}

    public ChatMemberId(Long roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    // Getters, Setters, hashCode, equals が必須
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMemberId that = (ChatMemberId) o;
        return Objects.equals(roomId, that.roomId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, userId);
    }
}