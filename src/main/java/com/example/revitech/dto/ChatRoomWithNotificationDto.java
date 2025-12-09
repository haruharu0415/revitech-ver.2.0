package com.example.revitech.dto;

import java.time.LocalDateTime;

import com.example.revitech.entity.ChatRoom;

import lombok.Data;

@Data
public class ChatRoomWithNotificationDto {

    private Integer roomId;
    private String name;
    private Integer type;
    private long unreadCount;
    private LocalDateTime lastMessageTimestamp;

    public ChatRoomWithNotificationDto(ChatRoom room, long unreadCount, LocalDateTime lastMessageTimestamp) {
        this.roomId = room.getRoomId();
        this.name = room.getName();
        this.type = room.getType();
        this.unreadCount = unreadCount;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}