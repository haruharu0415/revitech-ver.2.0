package com.example.revitech.dto;

import lombok.Data;

@Data
public class ChatRoomWithNotificationDto {
    private Integer roomId;
    private String roomName;
    private boolean hasUnread;

    public ChatRoomWithNotificationDto(Integer roomId, String roomName, boolean hasUnread) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.hasUnread = hasUnread;
    }
}