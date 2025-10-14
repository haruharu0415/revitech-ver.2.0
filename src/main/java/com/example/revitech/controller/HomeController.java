package com.example.revitech.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatMessageService;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.UsersService;

@Controller
public class HomeController {

    private final ChatMessageService chatMessageService;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService;

    public HomeController(ChatMessageService chatMessageService, 
                          UsersService usersService, 
                          ChatRoomService chatRoomService) { 
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService; 
    }

    // ホーム画面表示（role に応じて切り替え）
    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Users> optionalUser = usersService.findByEmail(email);
        if (optionalUser.isEmpty()) return "redirect:/login";

        Users user = optionalUser.get();
        model.addAttribute("user", user);

        switch (user.getRole().toUpperCase()) {
            case "ADMIN": return "home-admin";
            case "TEACHER": return "home-teacher";
            case "USER": // or STUDENT
            default: return "home";
        }
    }

    // DM画面表示
    @GetMapping("/dm")
    public String dmView(@RequestParam(name = "receiverId", required = false) Long receiverId, // 【修正】required = false に戻す
                         Model model) {
        
        // 【修正】receiverId が null の場合、エラー画面ではなくユーザー検索画面にリダイレクト
        if (receiverId == null) {
            return "redirect:/user-search"; 
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Users sender = usersService.findByEmail(email).orElseThrow(() -> new RuntimeException("Sender not found"));
        Users receiver = usersService.findById(receiverId).orElse(null);

        if (receiver == null) {
             // ユーザーが見つからない場合はリダイレクト
             return "redirect:/user-search"; 
        }

        // 1. ChatRoomServiceでDMルームを取得または作成
        ChatRoom room = chatRoomService.getOrCreateDmRoom(sender.getId(), receiverId);
        
        // 2. ルームIDを使ってメッセージ履歴を取得 (新ロジック)
        List<ChatMessage> messages = Collections.emptyList();
        if (room != null) {
            messages = chatMessageService.getMessagesByRoomId(room.getId());
        }
        
        // 3. Modelに情報を追加
        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("room", room);         
        model.addAttribute("messages", messages);
        
        return "dm";
    }

    // 【削除推奨】旧ロジックのメッセージ送信処理。WebSocket使用時は不要です。
    @PostMapping("/dm/send")
    public String sendMessage(@RequestParam("receiverStudentId") Long receiverId,
                              @RequestParam("content") String content) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users sender = usersService.findByEmail(auth.getName()).orElseThrow();

        // chatMessageService.sendMessage(sender.getId(), receiverId, content); // 古いメソッド

        return "redirect:/dm?receiverId=" + receiverId;
    }
}