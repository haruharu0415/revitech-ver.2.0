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

    @GetMapping("/chat-list")
    public String showChatList(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        boolean canCreateGroup = false;
        if (userOpt.isPresent()) {
            Integer role = userOpt.get().getRole();
            if (role == 2 || role == 3 || role == 9) {
                canCreateGroup = true;
            }
        }
        
        model.addAttribute("canCreateGroup", canCreateGroup);
        return "chat-list"; 
    }

    /**
     * ★★★ 追加・修正: グループ一覧画面 ★★★
     * 削除権限フラグ (canManageGroup) を渡します
     */
    @GetMapping("/group")
    public String showGroupList(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        boolean canManageGroup = false;
        if (userOpt.isPresent()) {
            Integer role = userOpt.get().getRole();
            // 先生(2)、管理者(3)、特権(9) は削除可能
            if (role == 2 || role == 3 || role == 9) {
                canManageGroup = true;
            }
        }
        
        model.addAttribute("canManageGroup", canManageGroup);
        return "group"; 
    }

    @GetMapping("/group-create")
    public String showGroupCreateForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        
        if (userOpt.isPresent()) {
            model.addAttribute("currentUserId", userOpt.get().getUsersId());
            return "group-create";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/chat-room/group/create")
    public String createGroup(@RequestParam("name") String name, @RequestParam("memberIds") List<Integer> memberIds) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users creator = usersService.findByEmail(auth.getName()).orElseThrow();
        ChatRoom group = chatRoomService.createGroupRoom(creator.getUsersId(), name, memberIds);
        return "redirect:/chat/room/" + group.getRoomId();
    }

    @PostMapping("/chat-room/delete/{roomId}")
    public String deleteGroup(@PathVariable Integer roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        if (userOpt.isPresent()) {
            Integer role = userOpt.get().getRole();
            if (role == 2 || role == 3 || role == 9) {
                chatRoomService.deleteGroupRoom(roomId);
            }
        }
        return "redirect:/chat-list";
    }

    @GetMapping("/chat/room/{roomId}")
    public String enterRoom(@PathVariable Integer roomId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        Users currentUser = userOpt.get();

        if (!chatRoomService.isUserMemberOfRoom(currentUser.getUsersId(), roomId)) {
            return "redirect:/home?error=access_denied";
        }

        Optional<ChatRoom> roomOpt = chatRoomService.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            return "redirect:/home?error=not_found";
        }

        model.addAttribute("userId", currentUser.getUsersId());
        model.addAttribute("userName", currentUser.getName());
        model.addAttribute("roomId", roomOpt.get().getRoomId());
        model.addAttribute("roomName", roomOpt.get().getName());
        
        chatRoomService.markRoomAsRead(currentUser.getUsersId(), roomId);

        return "group-chat";
    }
}