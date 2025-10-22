package com.example.revitech.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.repository.ChatMessageRepository; // ★ 修正済みのメソッドを持つ Repository

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository; // ★ 修正済みのメソッドを持つ Repository
    private final UsersService usersService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, UsersService usersService) {
        this.chatMessageRepository = chatMessageRepository;
        this.usersService = usersService;
    }

    // メッセージを送信（保存）する
    public ChatMessage sendMessage(Long roomId, Long senderUserId, String content) {
        ChatMessage message = new ChatMessage(roomId, senderUserId, content);
        return chatMessageRepository.save(message);
    }

    // 特定ルームのメッセージ履歴を DTO のリストとして取得する
    public List<ChatMessageDto> getMessagesByRoomId(Long roomId) {
        // ★★★ Repository のメソッド名を CreatedAt (キャメルケース) に修正 ★★★
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId).stream()
            .map(message -> {
                String senderName = usersService.findById(message.getSenderUserId())
                                                 .map(user -> user.getName())
                                                 .orElse("退会したユーザー");
                // ★ DTOのコンストラクタ内で getCreatedAt() が呼ばれることを確認
                return new ChatMessageDto(message, senderName);
            })
            .collect(Collectors.toList());
    }
}