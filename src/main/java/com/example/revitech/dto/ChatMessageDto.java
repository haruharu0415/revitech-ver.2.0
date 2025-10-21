package com.example.revitech.dto;

import java.time.LocalDateTime;

import com.example.revitech.entity.ChatMessage;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageDto {

    private Integer messageId;
    private Integer roomId;
    private Integer userId; // ★ usersIdから変更
    private String senderName;
    private String body;
    private LocalDateTime createdAt;

    public ChatMessageDto(ChatMessage message, String senderName) {
        this.messageId = message.getMessageId();
        this.roomId = message.getRoomId();
        this.userId = message.getUserId(); // ★ getUserId()を呼ぶように変更
        this.senderName = senderName;
        this.body = message.getBody();
        this.createdAt = message.getCreatedAt();
    }
}