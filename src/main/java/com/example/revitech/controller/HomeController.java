package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.ChatMessage;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.ChatMessageRepository;
import com.example.revitech.repository.UsersRepository;

@Controller
public class HomeController {

    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public HomeController(ChatMessageRepository chatMessageRepository, UsersRepository usersRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.usersRepository = usersRepository;
    }

    // ログインユーザーのホーム画面表示。roleで表示内容切り替え想定
    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Users> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }
        Users user = optionalUser.get();

        if ("TEACHER".equals(user.getRole())) {
            return "home-teacher";
        } else if ("STUDENT".equals(user.getRole())) {
            return "home-student";
        } else {
            return "home";
        }

    }

 // DM画面表示
    @GetMapping("/dm/chat")
    public String dmView(@RequestParam(name = "receiverId", required = false) Long receiverId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Users> senderOpt = usersRepository.findByEmail(email);
        if (senderOpt.isEmpty()) {
            return "redirect:/login";
        }
        Users sender = senderOpt.get();

        Users receiver = null;
        if (receiverId != null) {
            receiver = usersRepository.findById(receiverId).orElse(null);
        }

        List<ChatMessage> messages = chatMessageRepository.findChatBetweenUsers(sender.getId(), receiverId);

        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("messages", messages);

        return "dm";
    }

    // メッセージ送信処理
    @PostMapping("/send")
    public String sendMessage(@RequestParam("receiverStudentId") Long receiverId,
                              @RequestParam("content") String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Users> senderOpt = usersRepository.findByEmail(email);
        if (senderOpt.isEmpty()) {
            return "redirect:/login";
        }
        Users sender = senderOpt.get();

        ChatMessage message = new ChatMessage();
        message.setSenderStudentId(sender.getId()); // ここはIDを渡す
        message.setReceiverStudentId(receiverId);
        message.setContent(content);
        chatMessageRepository.save(message);

        return "redirect:/dm?receiverId=" + receiverId;
    }
}
