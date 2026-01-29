package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.dto.DmDisplayDto; // ★import追加
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.News;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.NewsService;
import com.example.revitech.service.ReviewService;
import com.example.revitech.service.UsersService;

@Controller
public class HomeController {

    private final UsersService usersService;
    private final ChatRoomService chatRoomService;
    private final NewsService newsService;
    private final ReviewService reviewService;

    public HomeController(UsersService usersService, ChatRoomService chatRoomService, NewsService newsService, ReviewService reviewService) {
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
        this.newsService = newsService;
        this.reviewService = reviewService;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal User loginUser, Model model) {
        Users user = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        model.addAttribute("user", user);

        // 管理者用通知
        if (user.getRole() == 3) {
            long pendingCount = usersService.findPendingUsers().size();
            model.addAttribute("pendingCount", pendingCount > 0 ? pendingCount : null);
            
            long disclosureCount = reviewService.countPendingDisclosureRequests();
            model.addAttribute("disclosureCount", disclosureCount > 0 ? disclosureCount : null);
            
            model.addAttribute("disclosureRequests", reviewService.getPendingDisclosureRequests());
        }

        // 先生用通知 (開示許可)
        if (user.getRole() == 2) {
            long grantedCount = reviewService.countGrantedDisclosuresForTeacher(user.getUsersId());
            model.addAttribute("grantedCount", grantedCount > 0 ? grantedCount : null);
            
            model.addAttribute("grantedDisclosures", reviewService.getGrantedDisclosuresForTeacher(user.getUsersId()));
        }

        // ニュース
        List<News> carouselNews = newsService.getTopNews(3);
        model.addAttribute("carouselNews", carouselNews);

        // チャット通知
        List<ChatRoom> unreadGroups = chatRoomService.findUnreadGroupRooms(user.getUsersId());
        model.addAttribute("unreadGroups", unreadGroups);

        // ★★★ 修正箇所: ここで List<DmDisplayDto> を受け取るように変更 ★★★
        List<DmDisplayDto> unreadDms = chatRoomService.findUnreadDmRooms(user.getUsersId());
        model.addAttribute("unreadDms", unreadDms);

        return "home";
    }
}