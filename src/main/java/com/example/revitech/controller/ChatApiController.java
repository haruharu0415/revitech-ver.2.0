package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ★ 通知情報を含むDTOをインポート
import com.example.revitech.dto.ChatRoomWithNotificationDto;
import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatApiController {

    private final ChatRoomService chatRoomService;
    private final UsersService usersService;

    public ChatApiController(ChatRoomService chatRoomService, UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
    }

    /**
     * ログインユーザーのルームリストを取得するAPI (通知機能付き)
     * 未読件数や最新メッセージ時刻を含むDTOのリストを返す。
     */
    @GetMapping("/my-rooms")
    // ★ 戻り値の型を List<ChatRoomWithNotificationDto> に変更
    public List<ChatRoomWithNotificationDto> getMyChatRooms() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = usersService.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        // ★ 通知情報を計算するサービスメソッドを呼び出す (引数は userId)
        return chatRoomService.getRoomsForUserWithNotifications(currentUser.getId());
    }

    /**
     * 特定ルームのメンバーリストを取得するAPI
     * @param roomId ChatRooms テーブルの主キー (room_id / Javaでは id)
     */
    @GetMapping("/{roomId}/members")
    public List<UserSearchDto> getRoomMembers(@PathVariable Long roomId) {
        // getRoomMembers の引数は roomId
        return chatRoomService.getRoomMembers(roomId);
    }
}