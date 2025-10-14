package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatApiController {

    private final ChatRoomService chatRoomService;
    private final UsersService usersService; // ★ ユーザー情報を取得するために追加

    // ★ コンストラクタを修正してUsersServiceを受け取る
    public ChatApiController(ChatRoomService chatRoomService, UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
    }

    /**
     * ★★★ これが新しく追加した、最も重要なエンドポイントです ★★★
     * ログインしているユーザーが所属するチャットルームのみを取得します。
     * @return 自分が所属するチャットルームのリスト
     */
    @GetMapping("/my-rooms")
    public List<ChatRoom> getMyChatRooms() {
        // 1. Spring Securityの機能を使って、現在ログインしているユーザーの情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // ログインID（今回はメールアドレス）を取得

        // 2. メールアドレスをキーにして、データベースから完全なユーザー情報を取得
        Users currentUser = usersService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("認証されたユーザーが見つかりません: " + email));

        // 3. 取得したユーザーIDを使って、その人が所属するルームだけをサービスから取得して返す
        return chatRoomService.getRoomsForUser(currentUser.getId());
    }

    /**
     * 特定のチャットルームに参加しているメンバーの一覧を取得します。
     * @param roomId メンバーを知りたいルームのID
     * @return メンバー情報のリスト (UserSearchDto形式)
     */
    @GetMapping("/{roomId}/members")
    public List<UserSearchDto> getRoomMembers(@PathVariable Long roomId) {
        return chatRoomService.getRoomMembers(roomId);
    }

    /**
     * (参考) 以前の、全てのチャットルームを取得するAPI。
     * これは誰でも全てのルームを見られてしまうため、管理者用などに用途を限定すべきです。
     * @return 全てのチャットルームのリスト
     */
    @GetMapping
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllRooms();
    }
}