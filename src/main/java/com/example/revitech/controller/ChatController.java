// src/main/java/com/example/revitech/controller/ChatController.java

package com.example.revitech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.entity.ChatMessage;
import com.example.revitech.repository.ChatMessageRepository;
import com.example.revitech.repository.UsersRepository;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UsersRepository usersRepository;

    // メッセージ送信（テスト用：受信者ID、送信者ID、内容を指定）
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestParam Long senderId,
                                         @RequestParam Long receiverId,
                                         @RequestParam String content) {
        // 送信者・受信者が存在するかチェック（簡易）
        if (!usersRepository.existsById(senderId) || !usersRepository.existsById(receiverId)) {
            return ResponseEntity.badRequest().body("Sender or receiver not found");
        }

        ChatMessage message = new ChatMessage(content, receiverId, senderId);
        chatMessageRepository.save(message);
        return ResponseEntity.ok("Message sent");
    }

    // 指定ユーザーの受信メッセージ一覧取得
    @GetMapping("/inbox")
    public ResponseEntity<List<ChatMessage>> getInbox(@RequestParam Long userId) {
        if (!usersRepository.existsById(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<ChatMessage> messages = chatMessageRepository.findByReceiverStudentId(userId);
        return ResponseEntity.ok(messages);
    }
}
