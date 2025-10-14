package com.example.revitech.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.UserSearchDto;
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
    
    // 【新規追加】特定のルームIDのメンバーを取得するAPI
    @GetMapping("/{roomId}/members")
    public List<UserSearchDto> getRoomMembers(@PathVariable Long roomId) {
        // ChatRoomServiceで実装したメソッドを呼び出す（メソッド名を修正）
        return chatRoomService.getRoomMembers(roomId);
    }
}