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
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService;

@Controller
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService;

    public ChatController(ChatRoomRepository chatRoomRepository, 
                          ChatGroupRepository chatGroupRepository,
                          UsersService usersService,
                          ChatRoomService chatRoomService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatGroupRepository = chatGroupRepository;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
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

        // 既読処理
        chatRoomService.markRoomAsRead(user.getUsersId(), roomId);

        // グループ(type=2)の場合
        if (room.getType() != null && room.getType() == 2) { 
            Optional<ChatGroup> groupOpt = chatGroupRepository.findById(roomId);
            if (groupOpt.isPresent()) {
                model.addAttribute("group", groupOpt.get());
                return "group-chat";
            }
        }

        // ★★★ 修正: DMの場合は、相手の名前を動的に取得して表示する ★★★
        // getDmPartnerNameを使って相手の名前を表示します
        String partnerName = chatRoomService.getDmPartnerName(roomId, user.getUsersId());
        
        model.addAttribute("roomId", room.getRoomId());
        model.addAttribute("chatName", partnerName); // 相手の名前をセット
        
        return "dm";
    }

    // グループチャット専用URL (一覧画面などで使用)
    @GetMapping("/group/chat/{groupId}")
    public String showGroupChat(@PathVariable("groupId") Integer groupId, Model model) {
        // 上の共通メソッドを呼び出すので、ここから入っても既読になります
        return showChatRoom(groupId, model);
    }
}