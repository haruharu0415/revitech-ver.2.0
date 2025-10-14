package com.example.revitech.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.dto.IncomingMessageDto; // ★ インポートを確認
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatMessageService;
import com.example.revitech.service.UsersService;

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UsersService usersService;

    public ChatWebSocketController(
            SimpMessageSendingOperations messagingTemplate,
            ChatMessageService chatMessageService,
            UsersService usersService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(IncomingMessageDto messageDto) { // ★★★ 引数の型を必ずこれにする
        
        logger.info("Received DTO: RoomId={}, SenderId={}, Content='{}'",
            messageDto.getRoomId(), messageDto.getSenderId(), messageDto.getContent());

        try {
            ChatMessage savedMessage = chatMessageService.sendMessage(
                messageDto.getRoomId(),
                messageDto.getSenderId(), // ★ DTOからIDを取得して渡す
                messageDto.getContent()
            );

            String senderName = usersService.findById(savedMessage.getSenderUserId())
                                             .map(Users::getName).orElse("不明");

            ChatMessageDto broadcastDto = new ChatMessageDto(savedMessage, senderName);

            messagingTemplate.convertAndSend("/topic/group/" + savedMessage.getRoomId(), broadcastDto);

        } catch (Exception e) {
            logger.error("CRITICAL ERROR in sendMessage: {}", e.getMessage(), e);
        }
    }
}