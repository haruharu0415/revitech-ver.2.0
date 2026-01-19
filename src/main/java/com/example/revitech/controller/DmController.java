package com.example.revitech.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
public class DmController {

    private final UsersService usersService;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    public DmController(UsersService usersService, 
                        ChatRoomService chatRoomService, 
                        ChatMessageService chatMessageService) {
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
        this.chatMessageService = chatMessageService;
    }

    /**
     * ユーザー検索などから遷移してくるDM画面
     * URL: /dm?receiverId=5 (パラメータ名を receiverId に統一)
     */
    @GetMapping("/dm")
    public String showDmPage(
            // ★修正: name="receiverId" に変更し、required=false に設定
            @RequestParam(name = "receiverId", required = false) Integer receiverId, 
            Model model, 
            @AuthenticationPrincipal User loginUser) {
        
        // ★修正: パラメータがない場合はエラーにせず、ユーザー検索画面へリダイレクト
        if (receiverId == null) {
            return "redirect:/user-search";
        }

        if (loginUser != null) {
            Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
            model.addAttribute("user", currentUser);

            // 相手の情報を取得
            Users receiver = usersService.findById(receiverId).orElse(null);
            if (receiver == null) {
                return "redirect:/user-search";
            }
            model.addAttribute("chatName", receiver.getName());

            // チャットルームを取得・作成
            ChatRoom room = chatRoomService.getOrCreateDmRoom(currentUser.getUsersId(), receiverId);
            
            if (room != null) {
                model.addAttribute("roomId", room.getRoomId());
                // メッセージ履歴を取得
                List<ChatMessageDto> messages = chatMessageService.getMessagesByRoomId(room.getRoomId());
                model.addAttribute("messages", messages);
            } else {
                model.addAttribute("roomId", 0);
                model.addAttribute("messages", Collections.emptyList());
            }
        }

        return "dm";
    }
}