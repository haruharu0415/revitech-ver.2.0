package com.example.revitech.dto;

import java.time.LocalDateTime;

import com.example.revitech.entity.ChatRoom;

public class ChatRoomWithNotificationDto {

    private Long id;
    private String name;
    private String type;
    private long unreadCount;           // 未読メッセージ件数
    private LocalDateTime lastMessageTimestamp; // 最新メッセージのタイムスタンプ

    public ChatRoomWithNotificationDto(ChatRoom room, long unreadCount, LocalDateTime lastMessageTimestamp) {
        this.id = room.getId();
        this.name = room.getName();
        this.type = room.getType();
        this.unreadCount = unreadCount;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    // Jackson (JSON変換) のためにGetterが必要
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public long getUnreadCount() { return unreadCount; }
    public LocalDateTime getLastMessageTimestamp() { return lastMessageTimestamp; }
}