package com.example.revitech.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

// import java.util.UUID; // ★ UUID は使わない

import com.example.revitech.dto.ChatMessageDto; // ★ senderUserId は Long
import com.example.revitech.dto.IncomingMessageDto; // ★ senderId は Long
import com.example.revitech.entity.ChatMessage; // ★ senderUserId は Long
import com.example.revitech.entity.Users; // ★ id は Long
import com.example.revitech.service.ChatMessageService; // ★ sendMessage の引数は Long
import com.example.revitech.service.ChatRoomService; // ★ markRoomAsRead の引数は Long
import com.example.revitech.service.UsersService; // ★ findById の引数は Long

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatWebSocketController(
            SimpMessageSendingOperations messagingTemplate,
            ChatMessageService chatMessageService,
            UsersService usersService,
            ChatRoomService chatRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(IncomingMessageDto messageDto) { // ★ DTO (senderId は Long)

        // ★ DTO から Long 型の senderId を取得 ★
        Long senderId = messageDto.getSenderId();
        Long roomId = messageDto.getRoomId();
        String content = messageDto.getContent();

        logger.info("Received message: RoomId={}, SenderId={}", roomId, senderId);

        if (senderId == null || roomId == null || content == null || content.isBlank()) {
             logger.warn("Invalid message received: {}", messageDto);
             return;
        }

        try {
            // 1. メッセージをDBに保存
            // ★ chatMessageService.sendMessage の第二引数に Long を渡す ★
            ChatMessage savedMessage = chatMessageService.sendMessage(
                roomId,
                senderId, // Long 型
                content
            );

            // 2. 送信者自身は既読にする
            // ★ chatRoomService.markRoomAsRead の第一引数に Long を渡す ★
            chatRoomService.markRoomAsRead(senderId, roomId);

            // 3. 送信者名を取得してDTOを作成
            // ★ usersService.findById の引数に Long を渡す ★
            String senderName = usersService.findById(senderId)
                                      .map(Users::getName)
                                      .orElse("不明なユーザー");

            // ★ ChatMessageDto コンストラクタ (senderUserId は Long) ★
            ChatMessageDto broadcastDto = new ChatMessageDto(savedMessage, senderName);

            // 4. ブロードキャスト
            messagingTemplate.convertAndSend("/topic/group/" + roomId, broadcastDto);

        } catch (Exception e) {
            logger.error("Error processing WebSocket message: RoomId={}, SenderId={}: {}",
                         roomId, senderId, e.getMessage(), e);
        }
    }
}