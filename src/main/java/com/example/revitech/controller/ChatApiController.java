package com.example.revitech.controller; 

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.revitech.dto.ChatMessageDto;
// ★★★ 修正点: DTO の import をあなたのファイル名に変更 ★★★
import com.example.revitech.dto.ChatRoomWithNotificationDto;
import com.example.revitech.dto.RoomMemberDto;
import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatMessageService;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService; 

@RestController
@RequestMapping("/api") 
public class ChatApiController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UsersService usersService;

    @Autowired
    public ChatApiController(ChatRoomService chatRoomService,
                             ChatMessageService chatMessageService,
                             UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
    }

    /**
     * ログイン中のユーザーが参加しているルーム一覧を取得
     */
    // ★★★ 修正点: 戻り値を List<ChatRoomWithNotificationDto> に変更 ★★★
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomWithNotificationDto>> getRooms() {
        Users currentUser = getAuthenticatedUser();
        
        List<ChatRoomWithNotificationDto> rooms = chatRoomService.getRoomsForUser(currentUser.getId()); 
        return ResponseEntity.ok(rooms);
    }

    /**
     * 特定のルームのメンバー一覧を取得
     */
    @GetMapping("/room/{roomId}/members")
    public ResponseEntity<List<RoomMemberDto>> getRoomMembers(@PathVariable Long roomId) {
        Users currentUser = getAuthenticatedUser();

        if (!chatRoomService.isUserMemberOfRoom(currentUser.getId(), roomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<RoomMemberDto> members = chatRoomService.getRoomMembers(roomId);
        return ResponseEntity.ok(members);
    }

    /**
     * 特定のルームのメッセージ履歴を取得
     */
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessages(@PathVariable Long roomId) {
        Users currentUser = getAuthenticatedUser();

        if (!chatRoomService.isUserMemberOfRoom(currentUser.getId(), roomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ChatMessageDto> messages = chatMessageService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    /**
     * ユーザー検索 (DM相手/グループメンバー検索用)
     */
    @GetMapping("/chat/search-users")
    public ResponseEntity<List<UserSearchDto>> searchUsers(@RequestParam String keyword) {
        Users currentUser = getAuthenticatedUser();
        
        List<UserSearchDto> users = usersService.findUsersByNameOrEmail(keyword).stream()
                .filter(dto -> !dto.getId().equals(currentUser.getId()))
                .toList();
                
        return ResponseEntity.ok(users);
    }


    /**
     * 認証済みのユーザーエンティティを取得するヘルパーメソッド
     */
    private Users getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return userOpt.get();
    }
}