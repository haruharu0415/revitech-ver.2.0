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

    /**
     * ルートパス("/")へのアクセスを処理します。
     * - 認証済みの場合は/homeへリダイレクト
     * - 未認証の場合は/loginへリダイレクト
     */
    @GetMapping("/")
    public String root() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 匿名ユーザーでなく、かつ認証済みの場合
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    /**
     * 認証済みのユーザーがアクセスするホームページ
     */
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

    /**
     * ダイレクトメッセージ(DM)画面を表示します。
     * @param receiverId メッセージ相手のユーザーID
     * @param model
     * @return
     */
    @GetMapping("/dm")
    public String dmView(@RequestParam(name = "receiverId", required = false) Integer receiverId, Model model) {
        if (receiverId == null) {
            return "redirect:/user-search"; // IDがなければユーザー検索へ
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users sender = usersService.findByEmail(auth.getName()).orElseThrow();
        Users receiver = usersService.findById(receiverId).orElse(null);

        if (receiver == null) {
            return "redirect:/user-search?error"; // 相手が見つからなければエラー表示
        }

        // 2人のユーザー間のDMルームを取得または作成
        ChatRoom room = chatRoomService.getOrCreateDmRoom(sender.getUsersId(), receiverId);

        // メッセージ履歴を取得
        List<ChatMessageDto> messages = (room != null) ?
            chatMessageService.getMessagesByRoomId(room.getRoomId()) : Collections.emptyList();

        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("room", room);
        model.addAttribute("messages", messages);

        // ★★★ ログインユーザーのIDを `userId` として渡す ★★★
        model.addAttribute("userId", sender.getUsersId());

        return "dm";
    }
}