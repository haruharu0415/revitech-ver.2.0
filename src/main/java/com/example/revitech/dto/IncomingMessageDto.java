package com.example.revitech.dto;

<<<<<<< HEAD
public class IncomingMessageDto {

    private Long roomId;
    private Long senderId;
    private String content;

    // Getters and Setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
=======
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
>>>>>>> 372bd0195d714990f90fd8ce9a4d2afebb696e88
}