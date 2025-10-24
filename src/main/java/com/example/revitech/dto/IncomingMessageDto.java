package com.example.revitech.dto;

// import java.util.UUID; // ★ UUID は使わない

public class IncomingMessageDto {

    private Long roomId;
    private Long senderId; // ★ 型を Long に戻す
    private String content;

    // Getters and Setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getSenderId() { return senderId; } // ★ 戻り値の型を Long に
    public void setSenderId(Long senderId) { this.senderId = senderId; } // ★ 引数の型を Long に

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}