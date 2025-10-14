package com.example.revitech.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.entity.ChatRoom;
import com.example.revitech.service.ChatRoomService;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatApiController {

    private final ChatRoomService chatRoomService;

    public ChatApiController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    // API用 全ルーム取得
    @GetMapping
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllRooms();
    }
}
