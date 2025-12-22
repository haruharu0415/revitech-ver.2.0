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
import com.example.revitech.entity.News;
import com.example.revitech.entity.TeacherReview; // 追加
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatMessageService;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.NewsService;
import com.example.revitech.service.ReviewService; // 追加
import com.example.revitech.service.UsersService;

@Controller
public class HomeController {

    private final ChatMessageService chatMessageService;
    private final UsersService usersService;
    private final ChatRoomService chatRoomService;
    private final NewsService newsService;
    private final ReviewService reviewService; // 追加

    // コンストラクタ修正
    public HomeController(ChatMessageService chatMessageService, 
                          UsersService usersService, 
                          ChatRoomService chatRoomService,
                          NewsService newsService,
                          ReviewService reviewService) {
        this.chatMessageService = chatMessageService;
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
        this.newsService = newsService;
        this.reviewService = reviewService;
    }

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
        
        Users loginUser = userOpt.get();
        model.addAttribute("user", loginUser);
        
        // --- 既存機能 ---
        List<News> carouselNews = newsService.findTopNewsForUser(loginUser, 3);
        model.addAttribute("carouselNews", carouselNews);

        List<ChatRoom> unreadGroups = chatRoomService.findUnreadGroupRooms(loginUser.getUsersId());
        List<ChatRoom> unreadDms = chatRoomService.findUnreadDmRooms(loginUser.getUsersId());
        model.addAttribute("unreadGroups", unreadGroups);
        model.addAttribute("unreadDms", unreadDms);
        
        // --- ★★★ 新規追加: 開示通知機能 ★★★ ---
        
        // 1. 管理者用 (Role=3): 開示請求の通知
        if (loginUser.getRole() == 3) {
            List<TeacherReview> disclosureRequests = reviewService.findPendingDisclosures();
            model.addAttribute("disclosureRequests", disclosureRequests);
        }
        
        // 2. 先生用 (Role=2): 開示許可の通知
        if (loginUser.getRole() == 2) {
            List<TeacherReview> grantedDisclosures = reviewService.findUncheckedGrantedDisclosures(loginUser.getUsersId());
            model.addAttribute("grantedDisclosures", grantedDisclosures);
        }
        
        return "home";
    }

    // dmメソッド等は変更なし
    @GetMapping("/dm")
    public String dmView(@RequestParam(name = "receiverId", required = false) Integer receiverId, Model model) {
        if (receiverId == null) return "redirect:/user-search";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users sender = usersService.findByEmail(auth.getName()).orElseThrow();
        Users receiver = usersService.findById(receiverId).orElse(null);
        if (receiver == null) return "redirect:/user-search?error";
        ChatRoom room = chatRoomService.getOrCreateDmRoom(sender.getUsersId(), receiverId);
        List<ChatMessageDto> messages = (room != null) ?
            chatMessageService.getMessagesByRoomId(room.getRoomId()) : Collections.emptyList();
        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("room", room);
        model.addAttribute("messages", messages);
        return "dm";
    }
}