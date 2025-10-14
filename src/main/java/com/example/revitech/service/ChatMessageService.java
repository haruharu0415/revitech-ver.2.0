package com.example.revitech.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.ChatMessage;
import com.example.revitech.repository.ChatMessageRepository;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    // 【修正】sendMessage メソッドが roomId ベースになっていること
    // このメソッドは ChatWebSocketController で利用されます。
    public ChatMessage sendMessage(Long roomId, Long senderUserId, String content) {
        // ChatMessage エンティティに (Long roomId, Long senderUserId, String content) 
        // を受け取るコンストラクタがあることを前提とします。
        ChatMessage message = new ChatMessage(roomId, senderUserId, content); 
        
        // データベースに保存
        return chatMessageRepository.save(message); 
    }

    // 【修正】メッセージ取得メソッドが roomId ベースになっていること
    // このメソッドは HomeController で利用されます。
    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        // Repositoryの findByRoomIdOrderByCreatedAtAsc(roomId) が必要です
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId); 
    }
}