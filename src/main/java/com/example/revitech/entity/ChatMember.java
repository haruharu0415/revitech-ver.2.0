package com.example.revitech.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_members")
public class ChatMember {

    @EmbeddedId // 複合主キーを使用
    private ChatMemberId id;

    // // 必要であれば、関連エンティティへのマッピングを追加
    // @ManyToOne
    // @MapsId("roomId") // id.roomIdに対応
    // @JoinColumn(name = "room_id")
    // private ChatRoom chatRoom;

    // @ManyToOne
    // @MapsId("userId") // id.userIdに対応
    // @JoinColumn(name = "users_id")
    // private Users user;

    public ChatMember() {}

    public ChatMember(ChatMemberId id) {
        this.id = id;
    }

    public ChatMember(Long roomId, Long userId) {
        this.id = new ChatMemberId(roomId, userId);
    }

    // Getters and Setters for id
    public ChatMemberId getId() { return id; }
    public void setId(ChatMemberId id) { this.id = id; }

    // id内のフィールドへの簡易アクセス用 (任意)
    public Long getRoomId() { return this.id != null ? this.id.getRoomId() : null; }
    public Long getUserId() { return this.id != null ? this.id.getUserId() : null; }
}