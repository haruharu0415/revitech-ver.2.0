package com.example.revitech.dto;

// ブラウザからのWebSocketメッセージを受け取るためだけの専用クラス
public class IncomingMessageDto {

    private Long roomId;
    private Long senderId; // ★ JavaScriptが送ってくるキー名 "senderId" に合わせる
    private String content;

    // --- 以下、GetterとSetter ---

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}