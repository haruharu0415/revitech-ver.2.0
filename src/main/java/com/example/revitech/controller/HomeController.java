package com.example.revitech.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
// import java.util.UUID; // ★ UUID は使わない

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.dto.ChatMessageDto; // ★ senderUserId は Long
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.Users; // ★ id は Long
import com.example.revitech.service.ChatMessageService; // ★ メソッド引数は Long
import com.example.revitech.service.ChatRoomService; // ★ メソッド引数は Long
import com.example.revitech.service.UsersService; // ★ findById(Long)

@Controller
public class HomeController {

    private final ChatMessageService chatMessageService;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService;

    @Autowired
    public HomeController(ChatMessageService chatMessageService,
                          UsersService usersService,
                          ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
    }

    // ホーム画面表示
    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        if (userOpt.isEmpty()) return "redirect:/login";

        Users user = userOpt.get();
        model.addAttribute("user", user); // ★ Users (id は Long) を渡す

        Integer userRole = user.getRole();
        if (userRole == null) return "home";

        return switch (userRole) {
            case 1 -> "home-admin";
            case 2 -> "home-teacher";
            default -> "home";
        };
    }

    // DM画面表示
    @GetMapping("/dm")
    // ★ receiverId の型を Long に戻す ★
    public String dmView(@RequestParam(name = "receiverId", required = false) Long receiverId, Model model) {
        if (receiverId == null) {
            return "redirect:/user-search?error=Receiver ID is required";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> senderOpt = usersService.findByEmail(auth.getName());
        if (senderOpt.isEmpty()) return "redirect:/login";
        Users sender = senderOpt.get();
        Long senderId = sender.getId(); // ★ 送信者ID (Long)

        // ★ receiver を Long で検索 ★
        Optional<Users> receiverOpt = usersService.findById(receiverId);
        if (receiverOpt.isEmpty()) {
            return "redirect:/user-search?error=Receiver not found";
        }
        Users receiver = receiverOpt.get();

        if (senderId.equals(receiverId)) {
             return "redirect:/user-search?error=Cannot DM yourself";
        }

        // ★ DMルームを取得/作成 (Long を渡す) ★
        ChatRoom room = chatRoomService.getOrCreateDmRoom(senderId, receiverId);

        List<ChatMessageDto> messages = (room != null) ?
            chatMessageService.getMessagesByRoomId(room.getId()) : Collections.emptyList();

        if (room != null) {
            // ★ markRoomAsRead に Long を渡す ★
            chatRoomService.markRoomAsRead(senderId, room.getId());
        }

        model.addAttribute("sender", sender);     // Users (id は Long)
        model.addAttribute("receiver", receiver);   // Users (id は Long)
        model.addAttribute("room", room);         // ChatRoom (id は Long)
        model.addAttribute("messages", messages); // List<ChatMessageDto> (senderUserId は Long)

        return "dm";
    }
}