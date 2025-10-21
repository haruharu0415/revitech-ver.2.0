package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_members")
@Data
@NoArgsConstructor
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    // ★ フィールド名を userId に修正
    @Column(name = "users_id", nullable = false)
    private Integer userId;

    // ★ コンストラクタの引数名も修正
    public ChatMember(Integer roomId, Integer userId) {
        this.roomId = roomId;
        this.userId = userId;
    }
}