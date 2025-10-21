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
import com.example.revitech.service.ChatRoomService; // ★ ChatRoomService をインポート
import com.example.revitech.service.UsersService;

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService; // ★ ChatRoomService を追加

    // ★ コンストラクタに ChatRoomService を追加
    public ChatWebSocketController(
            SimpMessageSendingOperations messagingTemplate,
            ChatMessageService chatMessageService,
            UsersService usersService,
            ChatRoomService chatRoomService) { // ★ 追加
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService; // ★ 追加
    }

    @MessageMapping("/chat.send")
    public void sendMessage(IncomingMessageDto messageDto) {
        logger.info("Received message: RoomId={}, SenderId={}", messageDto.getRoomId(), messageDto.getSenderId());
        try {
            // 1. メッセージをデータベースに保存
            ChatMessage savedMessage = chatMessageService.sendMessage(
                messageDto.getRoomId(),
                messageDto.getSenderId(),
                messageDto.getContent()
            );

            // ★★★ 2.【重要】メッセージを送信したユーザー自身はそのルームを既読にする ★★★
            chatRoomService.markRoomAsRead(savedMessage.getSenderUserId(), savedMessage.getRoomId());

            // 3. 送信者名を取得して、ブロードキャスト用のDTOを作成
            String senderName = usersService.findById(savedMessage.getSenderUserId())
                                             .map(Users::getName).orElse("不明");
            ChatMessageDto broadcastDto = new ChatMessageDto(savedMessage, senderName);

            // 4. 同じルームを購読している全員にメッセージを送信
            messagingTemplate.convertAndSend("/topic/group/" + savedMessage.getRoomId(), broadcastDto);

        } catch (Exception e) {
            logger.error("Error sending WebSocket message: {}", e.getMessage(), e);
            // 本番環境では、エラーをユーザーに通知する方法も検討
        }
    }
}