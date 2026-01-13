package com.example.revitech.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.revitech.entity.ChatGroup;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatGroupRepository;
import com.example.revitech.repository.ChatRoomRepository;
import com.example.revitech.service.UsersService;

@Controller
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final UsersService usersService;

    public ChatController(ChatRoomRepository chatRoomRepository, 
                          ChatGroupRepository chatGroupRepository,
                          UsersService usersService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatGroupRepository = chatGroupRepository;
        this.usersService = usersService;
    }

    /**
     * グループチャット画面を表示
     * URL: /group/chat/{groupId}
     * 対応ファイル: templates/group-chat.html
     */
    @GetMapping("/group/chat/{groupId}")
    public String showGroupChat(@PathVariable("groupId") Integer groupId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        Users user = usersService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);

        // グループ情報を取得
        Optional<ChatGroup> groupOpt = chatGroupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return "redirect:/group/list";
        }
        
        // 画面に "group" として渡す
        model.addAttribute("group", groupOpt.get());

        return "group-chat";
    }

    /**
     * 個人チャット(DM)画面を表示
     * URL: /chat/room/{roomId}
     * 対応ファイル: templates/chat-room.html
     */
    @GetMapping("/chat/room/{roomId}")
    public String showDmChat(@PathVariable("roomId") Integer roomId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        Users user = usersService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);

        // DM情報を取得
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return "redirect:/home";
        }
        
        model.addAttribute("chatRoom", roomOpt.get());

        return "chat-room";
    }
}