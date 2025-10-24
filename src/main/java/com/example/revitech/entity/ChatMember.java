package com.example.revitech.entity;

import java.util.Objects;

// import java.util.UUID; // ★ UUID は使わない
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_members")
public class ChatMember {

    @EmbeddedId
    private ChatMemberId id; // ★ 複合主キー (Long roomId, Long userId)

    // ChatRoom エンティティへの関連付け
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId") // ChatMemberId の roomId フィールドに対応
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    // Users エンティティへの関連付け
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // ChatMemberId の userId フィールドに対応
    @JoinColumn(name = "users_id")
    private Users user;

    // --- コンストラクタ ---
    public ChatMember() {}

    public ChatMember(ChatRoom room, Users user) {
        this.room = room;
        this.user = user;
        this.id = new ChatMemberId(room.getId(), user.getId()); // ★ ID を Long で設定
    }

    // --- Getters and Setters ---
    public ChatMemberId getId() { return id; }
    public void setId(ChatMemberId id) { this.id = id; }

    public ChatRoom getRoom() { return room; }
    public void setRoom(ChatRoom room) { this.room = room; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    // --- equals() と hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMember that = (ChatMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}