package com.example.revitech.dto;

public class ChatMessageDto {
    // 【修正点】DM専用の senderStudentId, receiverStudentId を削除
    
    private Long roomId;         // 【追加】ルームID
    private Long senderUserId;   // 【変更】senderStudentId -> senderUserId (DTOで使用するID)
    private String content;

    public ChatMessageDto() {}

    // Getter/Setter 
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public Long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}