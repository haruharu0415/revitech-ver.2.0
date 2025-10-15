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
    private final UsersService usersService;

    // UsersServiceも利用するため、コンストラクタで両方のサービスを受け取ります
    public ChatApiController(ChatRoomService chatRoomService, UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
    }

    /**
     * 【これが正しいAPI】
     * ログインしているユーザーが所属するチャットルームのみを取得します。
     * group.htmlはこのAPIを呼び出します。
     */
    @GetMapping("/my-rooms")
    public List<ChatRoom> getMyChatRooms() {
        // 現在ログイン中のユーザー情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // ユーザー情報からユーザーIDを特定
        Users currentUser = usersService.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        // 特定したユーザーIDで、その人が所属するルームだけを取得して返します
        return chatRoomService.getRoomsForUser(currentUser.getId());
    }
    
    /**
     * 特定のルームのメンバーリストを取得します。
     * group-chat.htmlがこのAPIを呼び出します。
     */
    @GetMapping("/{roomId}/members")
    public List<UserSearchDto> getRoomMembers(@PathVariable Long roomId) {
        return chatRoomService.getRoomMembers(roomId);
    }

    /**
     * 【注意】これは全てのチャットルームを返すAPIです。
     * 全員分のルーム名が見えてしまうため、通常のユーザー画面からは呼び出されません。
     * 管理者用の機能として残すか、不要であれば削除しても構いません。
     */
    @GetMapping
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllRooms();
    }
}