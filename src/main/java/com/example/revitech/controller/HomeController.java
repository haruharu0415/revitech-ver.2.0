package com.example.revitech.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.dto.ChatMessageDto; // ★ DTOをインポート
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

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        if (userOpt.isEmpty()) return "redirect:/login";
        
        Users user = userOpt.get();
        model.addAttribute("user", user);
        
        return switch (user.getRole().toUpperCase()) {
            case "ADMIN" -> "home-admin";
            case "TEACHER" -> "home-teacher";
            default -> "home";
        };
    }

    @GetMapping("/dm")
    public String dmView(@RequestParam(name = "receiverId", required = false) Long receiverId, Model model) {
        if (receiverId == null) return "redirect:/user-search";
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users sender = usersService.findByEmail(auth.getName()).orElseThrow();
        Users receiver = usersService.findById(receiverId).orElse(null);
        if (receiver == null) return "redirect:/user-search";
        
        ChatRoom room = chatRoomService.getOrCreateDmRoom(sender.getId(), receiverId);
        
        // ★★★ ここを修正 ★★★
        // 受け取る変数の型を List<ChatMessageDto> に変更します
        List<ChatMessageDto> messages = (room != null) ? 
            chatMessageService.getMessagesByRoomId(room.getId()) : Collections.emptyList();
        
        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("room", room);         
        model.addAttribute("messages", messages);
        
        return "dm";
    }
}