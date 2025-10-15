package com.example.revitech.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.ChatMessageDto; // ★ DTOをインポート
import com.example.revitech.service.ChatMessageService;

@RestController
@RequestMapping("/api/chat/messages")
public class ChatMessagesApiController {

    private final ChatMessageService chatMessageService;

    public ChatMessagesApiController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * ★★★ ここを修正 ★★★
     * このAPIが返すデータの型を List<ChatMessage> から List<ChatMessageDto> に変更します。
     */
    @GetMapping("/{roomId}")
    public List<ChatMessageDto> getMessages(@PathVariable Long roomId) {
        // 修正されたサービスメソッドを呼び出すと、自動的に正しい型のリストが返されます
        return chatMessageService.getMessagesByRoomId(roomId);
    }
}