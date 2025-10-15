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
    public void sendMessage(IncomingMessageDto messageDto) {
        logger.info("Received DTO: RoomId={}, SenderId={}", messageDto.getRoomId(), messageDto.getSenderId());
        try {
            ChatMessage savedMessage = chatMessageService.sendMessage(
                messageDto.getRoomId(),
                messageDto.getSenderId(),
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