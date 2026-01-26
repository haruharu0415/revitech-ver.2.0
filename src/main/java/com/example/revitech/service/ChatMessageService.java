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

    // メッセージ一覧取得
    public List<ChatMessageDto> getMessagesByRoomId(Integer roomId) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        
        return messages.stream().map(msg -> {
            // 送信者の名前を取得
            String senderName = usersRepository.findById(msg.getUserId())
                    .map(Users::getName)
                    .orElse("不明なユーザー");
            
            // DTOに変換（ここで content がコピーされます）
            return new ChatMessageDto(msg, senderName);
        }).collect(Collectors.toList());
    }

    // メッセージ送信（保存）
    public ChatMessage sendMessage(Integer roomId, Integer userId, String content) {
        ChatMessage message = new ChatMessage();
        message.setRoomId(roomId);
        message.setUserId(userId);
        message.setContent(content); // ★ここでDB保存用の箱に入れます
        // createdAtなどはEntityの@PrePersistやDBのデフォルト値で入る想定
        
        return chatMessageRepository.save(message);
    }
}