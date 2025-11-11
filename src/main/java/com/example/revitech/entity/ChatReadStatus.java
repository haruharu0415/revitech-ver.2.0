package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_read_status")
@Data
@NoArgsConstructor
public class ChatReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ★ フィールド名を userId に修正
    @Column(name = "users_id", nullable = false)
    private Integer userId;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    // ★ コンストラクタの引数名も修正
    public ChatReadStatus(Integer userId, Integer roomId) {
        this.userId = userId;
        this.roomId = roomId;
        this.lastReadAt = LocalDateTime.now();
    }
}