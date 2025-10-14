package com.example.revitech.controller;

// ★ 新規追加
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.revitech.entity.ChatMessage;
import com.example.revitech.service.ChatMessageService;

@Controller
public class ChatWebSocketController {

    // ★ 新規追加: ロガーの定義
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class); 

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;

    // ... (コンストラクタは省略) ...
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
        
        // ★ ログ追加: 受信したデータを確認
        logger.info("Received message: RoomId={}, SenderId={}, Content='{}'", 
            chatMessage.getRoomId(), 
            chatMessage.getSenderUserId(), 
            chatMessage.getContent());
        
        try { // ★ try-catch でデータベースエラーを捕捉
            // 1. データベースにメッセージを保存
            ChatMessage savedMessage = chatMessageService.sendMessage(
                chatMessage.getRoomId(), 
                chatMessage.getSenderUserId(), 
                chatMessage.getContent()
            );
            
            // ★ ログ追加: 保存成功を確認
            logger.info("Message saved successfully with ID: {}", savedMessage.getId());
            
            // 2. STOMPブローカーを通じて購読者にメッセージを送信
            // 送信先: /topic/messages/{roomId}
            messagingTemplate.convertAndSend(
                "/topic/messages/" + savedMessage.getRoomId(), 
                savedMessage
            );
            
        } catch (Exception e) {
            // ★ ログ追加: データベース保存やブロードキャスト失敗時のエラー詳細を出力
            logger.error("Failed to save or send message for RoomId {}: {}", chatMessage.getRoomId(), e.getMessage(), e);
            // ※ e.printStackTrace() ではなく logger.error() を使うのが Spring Boot の標準です。
        }
    }
}