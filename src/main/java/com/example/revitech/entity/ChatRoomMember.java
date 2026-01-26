package com.example.revitech.entity;

import java.time.LocalDateTime;

import com.example.revitech.entity.key.ChatRoomMemberId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "chat_room_members")
@IdClass(ChatRoomMemberId.class) // ★ここで複合キーを指定
@Data
public class ChatRoomMember {

    @Id
    @Column(name = "room_id")
    private Integer roomId;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @PrePersist
    public void onPrePersist() {
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
    }
}