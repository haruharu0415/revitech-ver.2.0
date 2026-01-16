package com.example.revitech.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class ChatMemberId implements Serializable {

    private Integer roomId;
    private Integer userId;

    public ChatMemberId() {}

    public ChatMemberId(Integer roomId, Integer userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    // ゲッター・セッター
    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // hashCode と equals は複合キーに必須です
    @Override
    public int hashCode() {
        return Objects.hash(roomId, userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChatMemberId other = (ChatMemberId) obj;
        return Objects.equals(roomId, other.roomId) && 
               Objects.equals(userId, other.userId);
    }
}