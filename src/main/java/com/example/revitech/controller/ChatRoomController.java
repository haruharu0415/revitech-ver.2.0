package com.example.revitech.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.entity.ChatRoom;
import com.example.revitech.repository.ChatRoomRepository;

@RestController
@RequestMapping("/api/chat-rooms")
@CrossOrigin(origins = "*") // フロントからのアクセス許可
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomController(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @GetMapping
    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll();
    }

    @PostMapping
    public ChatRoom createChatRoom(@RequestBody ChatRoom room) {
        return chatRoomRepository.save(room);
    }
}
