package com.example.revitech.dto;

import java.time.LocalDateTime;

import com.example.revitech.entity.ChatRoom;

public class ChatRoomWithNotificationDto {

    private Long id;
    private String name;
    // ★ String -> Integer に変更
    private Integer type;
    private long unreadCount;
    private LocalDateTime lastMessageTimestamp;

    // ★ コンストラクタ内の代入はそのまま (型が一致するため)
    public ChatRoomWithNotificationDto(ChatRoom room, long unreadCount, LocalDateTime lastMessageTimestamp) {
        this.id = room.getId();
        this.name = room.getName();
        this.type = room.getType(); // ChatRoomのgetType() は Integer を返す
        this.unreadCount = unreadCount;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    // ★ 戻り値の型を Integer に変更
    public Integer getType() { return type; }
    public long getUnreadCount() { return unreadCount; }
    public LocalDateTime getLastMessageTimestamp() { return lastMessageTimestamp; }
}