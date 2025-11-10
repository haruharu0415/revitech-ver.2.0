package com.example.revitech.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_members")
@Data
@NoArgsConstructor
public class ChatMember {

    @EmbeddedId
    private ChatMemberId id;

    public ChatMember(ChatMemberId id) {
        this.id = id;
    }
}