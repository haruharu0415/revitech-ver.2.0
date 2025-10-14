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

    // グループ作成処理 (変更なし)
    @PostMapping("/chat-room/group/create")
    public String createGroup(
            @RequestParam("name") String name,
            @RequestParam("memberIds") List<Long> memberIds,
            Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users creator = usersService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("ログインユーザーが見つかりません。"));
        Long creatorId = creator.getId();

        ChatRoom group = chatRoomService.createGroupRoom(creatorId, name, memberIds);
        
        return "redirect:/chat/room/" + group.getId();
    }


    /**
     * ★★★ これが画面真っ白問題を解決する、最も重要なメソッドです ★★★
     * チャットルームの画面を表示する際に、必要なデータを全てモデルに追加します。
     */
    @GetMapping("/chat/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, Model model) {
        
        // ★ 1. ログインユーザーのIDと名前をモデルに追加（これが無いとJSが動けません）
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            model.addAttribute("userId", user.getId());     // 必須
            model.addAttribute("userName", user.getName()); // 必須
        } else {
            // ログインしていない場合はアクセスさせない
            return "redirect:/login"; 
        }

        // ★ 2. 表示するチャットルームのIDと名前をモデルに追加（これが無いとJSが動けません）
        Optional<ChatRoom> roomOpt = chatRoomService.getRoomById(roomId);
        if (roomOpt.isPresent()) {
            ChatRoom room = roomOpt.get();
            model.addAttribute("roomId", room.getId());         // 必須
            model.addAttribute("roomName", room.getName()); // 必須
        } else {
            // 存在しないルームにアクセスしようとした場合はホームに戻す
            return "redirect:/home";
        }
        
        return "group-chat";
    }
}