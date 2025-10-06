package com.example.revitech.controller;

import java.util.List;

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

    // DM画面表示。receiverIdをクエリパラメータで受け取る想定
    @GetMapping("/dm/chat")
    public String dmView(@RequestParam(name = "receiverId", required = false) Long receiverId, Model model) {

        // ログインユーザー取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Users sender = usersRepository.findByEmail(email);

        // 受信者ユーザーも取得して名前表示用にセット（存在チェックも）
        Users receiver = null;
        if (receiverId != null) {
            receiver = usersRepository.findById(receiverId).orElse(null);
        }

        // 送信者と受信者間のチャットメッセージ一覧取得（双方のメッセージを取得）
        List<ChatMessage> messages = chatMessageRepository.findChatBetweenUsers(sender.getId(), receiverId);

        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("messages", messages);

        return "dm";
    }

    // メッセージ送信処理
    @PostMapping("/send")
    public String sendMessage(
            @RequestParam("receiverStudentId") Long receiverId,
            @RequestParam("content") String content) {

        // ログインユーザー取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Users sender = usersRepository.findByEmail(email);

        // 新規メッセージ保存
        ChatMessage message = new ChatMessage();
        message.setSenderStudentId(sender.getId());
        message.setReceiverStudentId(receiverId);
        message.setContent(content);
        chatMessageRepository.save(message);

        return "redirect:/dm?receiverId=" + receiverId;
    }
}
