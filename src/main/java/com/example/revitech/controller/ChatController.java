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
     * チャットルームへの入り口 (DM/グループ共通)
     * URL: /chat/room/{roomId}
     */
    @GetMapping("/chat/room/{roomId}")
    public String showChatRoom(@PathVariable("roomId") Integer roomId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        Users user = usersService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);

        // ルーム情報を取得
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return "redirect:/home";
        }
        ChatRoom room = roomOpt.get();

        // ★分岐処理: グループ(type=2)か、DM(それ以外)か
        if (room.getType() != null && room.getType() == 2) { 
            // グループチャットへ
            Optional<ChatGroup> groupOpt = chatGroupRepository.findById(roomId);
            if (groupOpt.isPresent()) {
                model.addAttribute("group", groupOpt.get()); // group-chat.html用
                return "group-chat";
            }
        }

        // DMチャットへ
        model.addAttribute("roomId", room.getRoomId()); // dm.html用
        model.addAttribute("chatName", room.getName()); // dm.html用
        return "dm";
    }

    /**
     * グループチャット専用URL (group-list.htmlなどで明示的に指定されている場合用)
     * URL: /group/chat/{groupId}
     */
    @GetMapping("/group/chat/{groupId}")
    public String showGroupChat(@PathVariable("groupId") Integer groupId, Model model) {
        return showChatRoom(groupId, model); // 上記の共通メソッドに処理を委譲
    }
}