package com.example.revitech.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.service.ChatMessageService; // 【修正点】RepositoryからServiceのDIに変更

@RestController
// 【修正点】DM専用パスを廃止し、メッセージ取得用の汎用パスに変更
@RequestMapping("/api/chat/messages") 
public class ChatMessagesApiController { // 【修正点】クラス名を変更 (DmController -> ChatMessagesApiController)

    private final ChatMessageService chatMessageService; 

    public ChatMessagesApiController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // GET: ルームIDでメッセージを取得
    // 【修正点】senderId/receiverIdの利用を廃止し、ルームIDをパスから取得
    @GetMapping("/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable Long roomId) {
        return chatMessageService.getMessagesByRoomId(roomId);
    }

    // POST: 新規メッセージ送信 (REST API経由)
    @PostMapping
    public ChatMessage sendMessage(@RequestBody ChatMessageDto dto) {
        
        // 【修正点】ServiceのsendMessageメソッドを利用 (roomId, senderUserId, content)
        ChatMessage saved = chatMessageService.sendMessage(
            dto.getRoomId(), 
            dto.getSenderUserId(), 
            dto.getContent()
        );

        System.out.println("Saved message ID: " + saved.getId());
        
        // WebSocket経由で送信するため、ここでは通知は行わない
        return saved;
    }
}