package com.example.revitech.dto;

import lombok.Data;

@Data
public class DmDisplayDto {
    private Integer roomId;
    private Integer partnerId;   // 相手のユーザーID
    private String partnerName;  // 相手の名前
    private String iconUrl;      // ★追加: アイコンURL

    public DmDisplayDto(Integer roomId, Integer partnerId, String partnerName, String iconUrl) {
        this.roomId = roomId;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.iconUrl = iconUrl;
    }
}