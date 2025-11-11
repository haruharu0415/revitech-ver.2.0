package com.example.revitech.dto;

import lombok.Data;

@Data
public class IncomingMessageDto {

    private Integer roomId;
    private Integer userId; // ★ usersIdから変更
    private String content;
}