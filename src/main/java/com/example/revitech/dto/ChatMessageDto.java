package com.example.revitech.dto;

import java.time.LocalDateTime;

import com.example.revitech.entity.ChatMessage;

// ★ フロントエンドとのデータ送受信専用のクラス
public class ChatMessageDto {

    private Long roomId;
    private Long senderUserId;
    private String senderName; // ★ 送信者名を追加
    private String content;
    private LocalDateTime createdAt;

    // Jackson (JSON変換ライブラリ) のためのデフォルトコンストラクタ
    public ChatMessageDto() {}

    // ★ ChatMessageエンティティと送信者名からDTOを生成するコンストラクタ
    public ChatMessageDto(ChatMessage message, String senderName) {
        this.roomId = message.getRoomId();
        this.senderUserId = message.getSenderUserId();
        this.senderName = senderName; // 名前をセット
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }

    // --- 以下、全てのフィールドに対するGetterとSetter ---

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}