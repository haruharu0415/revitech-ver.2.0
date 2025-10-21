package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Needed for getRoomMembers
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.ChatRoomWithNotificationDto; // DTO with notification info
import com.example.revitech.dto.UserSearchDto; // DTO for user info
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/chat-rooms") // Base path for all endpoints in this controller
public class ChatApiController {

    private final ChatRoomService chatRoomService;
    private final UsersService usersService;

    // Constructor injection for required services
    public ChatApiController(ChatRoomService chatRoomService, UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
    }

    /**
     * API endpoint to get the list of rooms for the currently logged-in user.
     * Returns data including unread counts and last message timestamps.
     * Accessed via: GET /api/chat-rooms/my-rooms
     */
    @GetMapping("/my-rooms")
    public List<ChatRoomWithNotificationDto> getMyChatRooms() { // Returns the DTO list
        // Get authentication details for the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Find the user entity based on the authenticated email (or principal name)
        Users currentUser = usersService.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found for authentication: " + auth.getName()));
        // Call the service method that calculates notifications
        return chatRoomService.getRoomsForUserWithNotifications(currentUser.getId());
    }

    /**
     * API endpoint to get the list of members for a specific chat room.
     * Accessed via: GET /api/chat-rooms/{roomId}/members
     * @param roomId The ID of the room (extracted from the URL path)
     * @return A list of UserSearchDto objects representing the members.
     */
    @GetMapping("/{roomId}/members")
    public List<UserSearchDto> getRoomMembers(@PathVariable Long roomId) {
        return chatRoomService.getRoomMembers(roomId);
    }

    /*
    // This endpoint returns ALL chat rooms.
    // It's generally not needed for regular users and could expose information.
    // Consider removing it or adding security restrictions (e.g., only for ADMIN role).
    @GetMapping
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.getAllRooms();
    }
    */
}