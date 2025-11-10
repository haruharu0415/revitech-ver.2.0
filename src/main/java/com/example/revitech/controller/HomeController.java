package com.example.revitech.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.dto.ChatMessageDto;
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

    public HomeController(ChatMessageService chatMessageService, UsersService usersService, ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/dm")
    public String dmView(@RequestParam(name = "receiverId", required = false) Integer receiverId, Model model) {
        if (receiverId == null) {
            return "redirect:/user-search";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users sender = usersService.findByEmail(auth.getName()).orElseThrow();
        
        // ★★★ ここを修正 ★★★
        // findById から findUserOrDummyById に変更
        Users receiver = usersService.findById(receiverId).orElse(null);

        if (receiver == null) {
            return "redirect:/user-search?error";
        }

        // receiverIdがダミーID(>100)の場合、DMルームはまだDBに無いので新規作成される
        ChatRoom room = chatRoomService.getOrCreateDmRoom(sender.getUsersId(), receiverId);
        
        List<ChatMessageDto> messages = (room != null) ?
            chatMessageService.getMessagesByRoomId(room.getRoomId()) : Collections.emptyList();

        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("room", room);
        model.addAttribute("messages", messages);

        return "dm";
    }

    // --- 以下の既存メソッドは変更ありません ---
    @GetMapping("/")
    public String root() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }
    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("user", userOpt.get());
        return "home";
    }
}