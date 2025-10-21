package com.example.revitech.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.service.ChatMessageService;

@RestController
@RequestMapping("/api/chat/messages")
public class ChatMessagesApiController {

    private final ChatMessageService chatMessageService;

    public ChatMessagesApiController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/{roomId}")
    public List<ChatMessageDto> getMessages(@PathVariable Integer roomId) {
        return chatMessageService.getMessagesByRoomId(roomId);
    }
}