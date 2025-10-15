package com.example.revitech.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.repository.ChatMessageRepository;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UsersService usersService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, UsersService usersService) {
        this.chatMessageRepository = chatMessageRepository;
        this.usersService = usersService;
    }

    public ChatMessage sendMessage(Long roomId, Long senderUserId, String content) {
        ChatMessage message = new ChatMessage(roomId, senderUserId, content);
        return chatMessageRepository.save(message);
    }

    public List<ChatMessageDto> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId).stream()
            .map(message -> {
                String senderName = usersService.findById(message.getSenderUserId())
                                                 .map(user -> user.getName())
                                                 .orElse("退会したユーザー");
                return new ChatMessageDto(message, senderName);
            })
            .collect(Collectors.toList());
    }
}