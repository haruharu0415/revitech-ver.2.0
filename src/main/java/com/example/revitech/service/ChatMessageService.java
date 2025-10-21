package com.example.revitech.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMessageRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, UsersRepository usersRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.usersRepository = usersRepository;
    }

    // ★ 修正: 引数名を userId に変更
    public ChatMessage sendMessage(Integer roomId, Integer userId, String content) {
        // ★ 修正: ChatMessage のコンストラクタ引数を修正
        ChatMessage message = new ChatMessage(roomId, userId, content);
        return chatMessageRepository.save(message);
    }

    public List<ChatMessageDto> getMessagesByRoomId(Integer roomId) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        return messages.stream()
            .map(message -> {
                // ★ 修正: message.getUserId() を使用
                String senderName = usersRepository.findById(message.getUserId())
                                                 .map(Users::getName)
                                                 .orElse("不明なユーザー");
                return new ChatMessageDto(message, senderName);
            })
            .collect(Collectors.toList());
    }
}