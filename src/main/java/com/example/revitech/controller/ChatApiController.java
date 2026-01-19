package com.example.revitech.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.ChatMessageDto;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatMessageService;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatApiController {

    private final UsersService usersService;
    private final ChatMessageService chatMessageService; // ★追加: メッセージ操作用サービス

    public ChatApiController(UsersService usersService, ChatMessageService chatMessageService) {
        this.usersService = usersService;
        this.chatMessageService = chatMessageService;
    }

    // =================================================================
    // ★★★ 重要: 以下のメソッドはコメントアウトのままにしてください ★★★
    // ChatRoomRestController と競合するため
    // =================================================================
    /*
    @GetMapping("/my-rooms")
    public List<Map<String, Object>> getMyChatRooms(@AuthenticationPrincipal User loginUser) {
        return null;
    }
    */

    /**
     * チャットルームのメッセージ履歴を取得
     * URL: /api/chat-rooms/{roomId}/messages
     */
    @GetMapping("/{roomId}/messages")
    public List<ChatMessageDto> getMessages(@PathVariable Integer roomId, @AuthenticationPrincipal User loginUser) {
        // ★修正: 空リストではなく、DBから本当のメッセージ履歴を取得して返す
        return chatMessageService.getMessagesByRoomId(roomId);
    }

    /**
     * メッセージを送信
     * URL: /api/chat-rooms/{roomId}/send
     */
    @PostMapping("/{roomId}/send")
    public void sendMessage(@PathVariable Integer roomId, 
                            @RequestBody Map<String, String> payload,
                            @AuthenticationPrincipal User loginUser) {
        // ★修正: 受け取ったメッセージをDBに保存する
        String content = payload.get("message");
        
        // 入力チェック
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        Users sender = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        
        // サービスを使って保存
        chatMessageService.sendMessage(roomId, sender.getUsersId(), content);
    }
}