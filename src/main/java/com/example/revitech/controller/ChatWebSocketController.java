package com.example.revitech.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.dto.IncomingMessageDto;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatMessageService;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService;

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService;

    public ChatWebSocketController(SimpMessageSendingOperations messagingTemplate, ChatMessageService chatMessageService, UsersService usersService, ChatRoomService chatRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(IncomingMessageDto messageDto) {
        // ★ 修正: messageDto.getUserId() を使用
        logger.info("Received message: RoomId={}, SenderId={}", messageDto.getRoomId(), messageDto.getUserId());
        try {
            // ★ 修正: messageDto.getUserId() を渡す
            ChatMessage savedMessage = chatMessageService.sendMessage(
                messageDto.getRoomId(),
                messageDto.getUserId(),
                messageDto.getContent()
            );

            // ★ 修正: savedMessage.getUserId() を使用
            chatRoomService.markRoomAsRead(savedMessage.getUserId(), savedMessage.getRoomId());

            // ★ 修正: savedMessage.getUserId() を使用
            String senderName = usersService.findById(savedMessage.getUserId())
                                             .map(Users::getName).orElse("不明なユーザー");
            ChatMessageDto broadcastDto = new ChatMessageDto(savedMessage, senderName);
            
            messagingTemplate.convertAndSend("/topic/room/" + savedMessage.getRoomId(), broadcastDto);

        } catch (Exception e) {
            logger.error("Error sending WebSocket message: {}", e.getMessage(), e);
        }
    }
}