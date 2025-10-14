package com.example.revitech.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.revitech.entity.ChatMessage;
import com.example.revitech.service.ChatMessageService;

// 【重要】クライアントからのメッセージ送信に使用するDTO（データ転送オブジェクト）
// ChatMessageService.sendMessage の引数に合わせるため、ここでは ChatMessage エンティティを直接使用します。
// （通常は ChatMessageDTO を使用することが推奨されますが、ここでは簡略化のためChatMessageを使用）

@Controller
public class ChatWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;

    public ChatWebSocketController(SimpMessageSendingOperations messagingTemplate, ChatMessageService chatMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
    }

    /**
     * クライアントから /app/chat.send にメッセージが送信された時に呼ばれる
     * @param chatMessage クライアントから送られてきたメッセージオブジェクト
     */
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage chatMessage) {
        
        // 1. データベースにメッセージを保存
        // chatMessageService.sendMessage(roomId, senderUserId, content) を呼び出す
        ChatMessage savedMessage = chatMessageService.sendMessage(
            chatMessage.getRoomId(), 
            chatMessage.getSenderUserId(), 
            chatMessage.getContent()
        );
        
        // 2. STOMPブローカーを通じて購読者にメッセージを送信
        // 送信先: /topic/messages/{roomId}
        // ここで送信することで、ルームにいる全メンバーにメッセージがリアルタイムで届く
        messagingTemplate.convertAndSend(
            "/topic/messages/" + savedMessage.getRoomId(), 
            savedMessage
        );
    }
}