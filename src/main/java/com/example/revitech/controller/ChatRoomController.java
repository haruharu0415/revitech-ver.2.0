package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService;

@Controller
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UsersService usersService;

    public ChatRoomController(ChatRoomService chatRoomService, UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService;
    }

    @PostMapping("/chat-room/group/create")
    public String createGroup(@RequestParam("name") String name, @RequestParam("memberIds") List<Long> memberIds) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users creator = usersService.findByEmail(auth.getName()).orElseThrow();
        ChatRoom group = chatRoomService.createGroupRoom(creator.getId(), name, memberIds);
        return "redirect:/chat/room/" + group.getId();
    }

    @GetMapping("/chat/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        Users currentUser = userOpt.get();

        if (!chatRoomService.isUserMemberOfRoom(currentUser.getId(), roomId)) {
            return "redirect:/home?error=access_denied"; 
        }

        Optional<ChatRoom> roomOpt = chatRoomService.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            return "redirect:/home?error=not_found";
        }

        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("userName", currentUser.getName());
        model.addAttribute("roomId", roomOpt.get().getId());
        model.addAttribute("roomName", roomOpt.get().getName());
        chatRoomService.markRoomAsRead(currentUser.getId(), roomId);
        return "group-chat";
    }
}