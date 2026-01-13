package com.example.revitech.entity.key;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

@Data
public class ChatRoomMemberId implements Serializable {
    private Integer roomId;
    private Integer userId;

    public ChatRoomMemberId() {}

    public ChatRoomMemberId(Integer roomId, Integer userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    // 複合キーには equals と hashCode が必須です
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoomMemberId that = (ChatRoomMemberId) o;
        return Objects.equals(roomId, that.roomId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, userId);
    }
}