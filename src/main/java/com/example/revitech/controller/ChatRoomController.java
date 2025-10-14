package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication; // 【追加】
import org.springframework.security.core.context.SecurityContextHolder; // 【追加】
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users; // 【追加】
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService; // 【追加】

@Controller
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UsersService usersService; // 【追加】

    // 【修正】コンストラクタにUsersServiceを追加
    public ChatRoomController(ChatRoomService chatRoomService, UsersService usersService) {
        this.chatRoomService = chatRoomService;
        this.usersService = usersService; // 【追加】
    }

    @PostMapping("/chat-room/group/create")
    public String createGroup(
                              // Long creatorId の @RequestParam を削除
                              @RequestParam("name") String name,
                              @RequestParam("memberIds") List<Long> memberIds,
                              Model model) {
        
        // 【最重要修正箇所】認証コンテキストからログインユーザーIDを取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users creator = usersService.findByEmail(auth.getName())
                            .orElseThrow(() -> new RuntimeException("ログインユーザーが見つかりません。"));

        Long creatorId = creator.getId(); // 認証されたユーザーのIDを取得

        // Serviceメソッドの呼び出し
        ChatRoom group = chatRoomService.createGroupRoom(creatorId, name, memberIds);
        
        model.addAttribute("room", group);
        return "redirect:/chat/room/" + group.getId();
    }


    // ルーム画面表示（グループチャット詳細として group-chat.html を表示）
    @GetMapping("/chat/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, Model model) {
        
        // 1. ログインユーザー情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Users> userOpt = usersService.findByEmail(email);

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            model.addAttribute("userId", user.getId()); 
            model.addAttribute("userName", user.getName()); // 画面表示用の名前
        } else {
            // エラーハンドリング (通常は発生しないはず)
            return "redirect:/login"; 
        }

        // 2. ChatRoom情報を取得
        Optional<ChatRoom> roomOpt = chatRoomService.getRoomById(roomId);
        if (roomOpt.isPresent()) {
            ChatRoom room = roomOpt.get();
            model.addAttribute("roomId", roomId); 
            model.addAttribute("roomName", room.getName()); // ルーム名をModelに追加
        } else {
            // ルームが見つからない場合のエラーハンドリング
            model.addAttribute("roomId", roomId);
            model.addAttribute("roomName", "不明なチャットルーム");
        }
        
        return "group-chat"; // group-chat.html がレンダリングされる
    }
}