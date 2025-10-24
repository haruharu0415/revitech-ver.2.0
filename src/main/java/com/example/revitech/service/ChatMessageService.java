package com.example.revitech.service;

import java.util.List;
// import java.util.UUID; // ★ UUID は使わない
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.ChatMessageDto; // ★ senderUserId は Long
import com.example.revitech.entity.ChatMessage; // ★ senderUserId は Long
import com.example.revitech.repository.ChatMessageRepository; // ★ findByRoomIdOrderByCreatedAtAsc(Long)

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UsersService usersService;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, UsersService usersService) {
        this.chatMessageRepository = chatMessageRepository;
        this.usersService = usersService;
    }

    /** メッセージ送信 (Long) */
    // ★ senderUserId の型を Long に戻す ★
    public ChatMessage sendMessage(Long roomId, Long senderUserId, String content) {
        // ★ ChatMessage コンストラクタは Long を受け取る ★
        ChatMessage message = new ChatMessage(roomId, senderUserId, content);
        return chatMessageRepository.save(message);
    }

    /** メッセージ履歴取得 (Long) */
    public List<ChatMessageDto> getMessagesByRoomId(Long roomId) {
        // ★ findByRoomIdOrderByCreatedAtAsc は Long を受け取る ★
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);

        return messages.stream()
            .map(message -> {
                // ★ message.getSenderUserId() は Long を返す ★
                // ★ usersService.findById() は Long を受け取る ★
                String senderName = usersService.findById(message.getSenderUserId())
                                          .map(user -> user.getName())
                                          .orElse("退会したユーザー");

                // ★ ChatMessageDto のコンストラクタは ChatMessage (senderUserId は Long) を受け取る ★
                return new ChatMessageDto(message, senderName);
            })
            .collect(Collectors.toList());
    }
}