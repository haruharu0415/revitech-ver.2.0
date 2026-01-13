package com.example.revitech.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.entity.ChatGroup;
import com.example.revitech.service.GroupService;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatRoomRestController {

    private final GroupService groupService;
    private final UsersService usersService;

    public ChatRoomRestController(GroupService groupService, UsersService usersService) {
        this.groupService = groupService;
        this.usersService = usersService;
    }

    @GetMapping("/my-rooms")
    public List<Map<String, Object>> getMyRooms(@AuthenticationPrincipal User loginUser) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (loginUser == null) return results;

        // DBから全グループを取得
        List<ChatGroup> groups = groupService.getAllGroups();

        for (ChatGroup group : groups) {
            Map<String, Object> room = new HashMap<>();
            room.put("roomId", group.getGroupId());
            room.put("name", group.getGroupName());
            room.put("type", 2); // 2 = GROUP
            room.put("unreadCount", 0);
            room.put("lastMessageTimestamp", null);
            results.add(room);
        }
        return results;
    }
}