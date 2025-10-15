// HomeController.java の全文
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

    // ▼▼▼【修正箇所】homeメソッド全体を修正 ▼▼▼
    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 認証されておらず、匿名ユーザーの場合
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            // ログインしていないユーザー向けのホーム画面を表示
            // ゲストユーザーであることを示す属性をModelに追加することもできます
            // model.addAttribute("isGuest", true);
            return "home";
        }

        // --- 以下、認証済みユーザーの場合の処理 ---
        String email = auth.getName();
        Optional<Users> optionalUser = usersService.findByEmail(email);
        
        // 認証情報はあるが、DBにユーザーが存在しない場合
        if (optionalUser.isEmpty()) {
            // ゲスト用のホーム画面にフォールバックします
            return "home"; 
        }

        Users user = optionalUser.get();
        model.addAttribute("user", user);

        // 役割に応じた画面振り分け
        switch (user.getRole().toUpperCase()) {
            case "ADMIN": return "home-admin";
            case "TEACHER": return "home-teacher";
            case "USER": // or STUDENT
            default: return "home";
        }
    }

    // DM画面表示
    @GetMapping("/dm")
    public String dmView(@RequestParam(name = "receiverId", required = false) Long receiverId,
                         Model model) {
        
        if (receiverId == null) {
            return "redirect:/user-search"; 
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Users sender = usersService.findByEmail(email).orElseThrow(() -> new RuntimeException("Sender not found"));
        Users receiver = usersService.findById(receiverId).orElse(null);

        if (receiver == null) {
             return "redirect:/user-search"; 
        }

        ChatRoom room = chatRoomService.getOrCreateDmRoom(sender.getId(), receiverId);
        
        List<ChatMessage> messages = Collections.emptyList();
        if (room != null) {
            messages = chatMessageService.getMessagesByRoomId(room.getId());
        }
        
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

        return "redirect:/dm?receiverId=" + receiverId;
    }
}