// ChatWebSocketController.java

package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.revitech.dto.UserSearchDto;      // 【追加】
import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.Users;          // 【追加】
import com.example.revitech.service.ChatMessageService;
import com.example.revitech.service.ChatRoomService; // 【追加】
import com.example.revitech.service.UsersService;    // 【追加】

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class); 

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService; // 【追加】
    private final UsersService usersService;       // 【追加】

    // 【修正】コンストラクタにサービスを追加
    public ChatWebSocketController(SimpMessageSendingOperations messagingTemplate, 
                                   ChatMessageService chatMessageService,
                                   ChatRoomService chatRoomService, // 【追加】
                                   UsersService usersService) {      // 【追加】
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
    }

    /**
     * クライアントから /app/chat.send にメッセージが送信された時に呼ばれる
     * @param chatMessage クライアントから送られてきたメッセージオブジェクト
     */
    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage chatMessage) {
        
        logger.info("Received message: RoomId={}, SenderId={}, Content='{}'", 
            chatMessage.getRoomId(), 
            chatMessage.getSenderUserId(), 
            chatMessage.getContent());
        
        try { 
            // 1. データベースにメッセージを保存
            ChatMessage savedMessage = chatMessageService.sendMessage(
                chatMessage.getRoomId(), 
                chatMessage.getSenderUserId(), 
                chatMessage.getContent()
            );
            
            logger.info("Message saved successfully with ID: {}", savedMessage.getId());
            
            // 2. STOMPブローカーを通じてチャットルームの購読者にメッセージを送信
            // 送信先: /topic/messages/{roomId}
            messagingTemplate.convertAndSend(
                "/topic/messages/" + savedMessage.getRoomId(), 
                savedMessage
            );

            // ===================================================================
            // 【新規実装】ホーム画面通知ロジック
            // ===================================================================
            
            // 2-1. 送信者とルーム情報を取得
            Long senderId = savedMessage.getSenderUserId();
            Long roomId = savedMessage.getRoomId();
            
            Optional<Users> senderOpt = usersService.findById(senderId);
            if (senderOpt.isEmpty()) {
                logger.error("Sender user not found for ID: {}", senderId);
                return; 
            }
            Users sender = senderOpt.get();

            // ルーム名を取得 (DMの場合は相手の名前になるロジックなどが必要だが、ここではルーム名またはデフォルト名を使用)
            Optional<String> roomNameOpt = chatRoomService.getRoomById(roomId)
                                            .map(room -> room.getName() != null && !room.getName().isEmpty() 
                                                        ? room.getName() : "チャットルーム");
            String roomName = roomNameOpt.orElse("チャットルーム");
            
            // 2-2. ルームのメンバーを取得し、送信者を除外
            List<UserSearchDto> roomMembers = chatRoomService.getRoomMembers(roomId);
            
            // 2-3. 通知データ構造を作成 (JSON文字列)
            String rawContent = savedMessage.getContent();
            String snippetContent = rawContent.length() > 20 ? rawContent.substring(0, 20) + "..." : rawContent;
            
            String notificationMessage = String.format("{\"senderName\":\"%s\", \"roomName\":\"%s\", \"roomId\":%d, \"content\":\"%s\"}",
                                                       sender.getName(),
                                                       roomName,
                                                       roomId,
                                                       snippetContent.replace("\"", "\\\"").replace("\n", " ")); // JSONとして安全にする
            
            // 2-4. 各メンバーのプライベートキューに通知を送信
            for (UserSearchDto member : roomMembers) {
                // 送信者自身には通知を送らない
                if (!member.getId().equals(senderId)) { 
                    // プライベートな通知キュー: /user/{email}/queue/notifications
                    // SpringがPrincipal (ここではユーザーのメールアドレス) を使ってルーティングする
                    String destination = "/queue/notifications"; 
                    messagingTemplate.convertAndSendToUser(
                        member.getEmail(), // 購読時に使用した Principal名 (email) を指定
                        destination, 
                        notificationMessage
                    );
                    logger.info("Sent notification to user {}. RoomId: {}", member.getEmail(), roomId);
                }
            }
            // ===================================================================
            
        } catch (Exception e) {
            logger.error("Failed to save or send message for RoomId {}: {}", chatMessage.getRoomId(), e.getMessage(), e);
        }
    }
}