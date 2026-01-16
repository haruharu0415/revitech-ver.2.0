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
import com.example.revitech.service.ChatRoomService; // ★追加
import com.example.revitech.service.UsersService;

@Controller
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService; // ★追加

    public ChatController(ChatRoomRepository chatRoomRepository, 
                          ChatGroupRepository chatGroupRepository,
                          UsersService usersService,
                          ChatRoomService chatRoomService) { // ★コンストラクタに追加
        this.chatRoomRepository = chatRoomRepository;
        this.chatGroupRepository = chatGroupRepository;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService; // ★追加
    }

    // チャット画面への入り口 (DM・グループ共通)
    @GetMapping("/chat/room/{roomId}")
    public String showChatRoom(@PathVariable("roomId") Integer roomId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        Users user = usersService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);

        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return "redirect:/home";
        }
        ChatRoom room = roomOpt.get();

        // ★★★ ここが重要！チャットを開いた時点で「既読」にする ★★★
        // これにより、通知欄からでも一覧からでも、開けば通知が消えます
        chatRoomService.markRoomAsRead(user.getUsersId(), roomId);

        // ★分岐: グループ(type=2)なら group-chat.html、それ以外は dm.html
        if (room.getType() != null && room.getType() == 2) { 
            Optional<ChatGroup> groupOpt = chatGroupRepository.findById(roomId);
            if (groupOpt.isPresent()) {
                model.addAttribute("group", groupOpt.get());
                return "group-chat";
            }
        }

        // DMの場合
        model.addAttribute("roomId", room.getRoomId());
        model.addAttribute("chatName", room.getName());
        return "dm";
    }

    // グループチャット専用URL (一覧画面などで使用)
    @GetMapping("/group/chat/{groupId}")
    public String showGroupChat(@PathVariable("groupId") Integer groupId, Model model) {
        // 上の共通メソッドを呼び出すので、ここから入っても既読になります
        return showChatRoom(groupId, model);
    }
}