// src/main/java/com/example/revitech/controller/DmController.java

package com.example.revitech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.ChatMessage;
import com.example.revitech.repository.ChatMessageRepository;

@Controller
public class DmController {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public DmController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping("/dm")
    public String showDmPage(Model model) {
        List<ChatMessage> messages = chatMessageRepository.findAll(); // 全メッセージ取得
        model.addAttribute("messages", messages);
        return "dm";
    }

    @PostMapping("/dm/send")
    public String sendMessage(@RequestParam("senderStudentId") Long senderId,
                              @RequestParam("receiverStudentId") Long receiverId,
                              @RequestParam("content") String content) {
        ChatMessage message = new ChatMessage(content, receiverId, senderId);
        chatMessageRepository.save(message);
        return "redirect:/dm";
    }
    //ss
}
