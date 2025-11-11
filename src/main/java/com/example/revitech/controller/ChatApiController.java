package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/my-rooms")
    public List<ChatRoomWithNotificationDto> getMyChatRooms() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = usersService.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found for authentication: " + auth.getName()));
        return chatRoomService.getRoomsForUserWithNotifications(currentUser.getUsersId());
    }

    @GetMapping("/{roomId}/members")
    public List<UserSearchDto> getRoomMembers(@PathVariable Integer roomId) {
        return chatRoomService.getRoomMembers(roomId);
    }
}