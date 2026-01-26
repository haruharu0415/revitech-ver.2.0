package com.example.revitech.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
// ★重要: データが入っている正しいテーブル名「chat_members」を指定
@Table(name = "chat_members") 
public class ChatMember {

    @EmbeddedId
    // ★重要: DBの列名 "users_id" と "room_id" に合わせる設定
    @AttributeOverride(name = "userId", column = @Column(name = "users_id"))
    @AttributeOverride(name = "roomId", column = @Column(name = "room_id"))
    private ChatMemberId id;

    // コンストラクタ
    public ChatMember() {}

    public ChatMember(ChatMemberId id) {
        this.id = id;
    }

    // ゲッター・セッター
    public ChatMemberId getId() {
        return id;
    }

    public void setId(ChatMemberId id) {
        this.id = id;
    }
}