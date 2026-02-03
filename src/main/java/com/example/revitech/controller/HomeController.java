package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.dto.DmDisplayDto;
import com.example.revitech.entity.ChatRoom;
import com.example.revitech.entity.News;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.NewsService;
import com.example.revitech.service.ReviewService;
import com.example.revitech.service.UsersService;

import jakarta.servlet.http.HttpSession;

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

    @SuppressWarnings("unchecked")
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session) {
        Users user = null;
        if (userDetails != null) {
            user = usersService.findByEmail(userDetails.getUsername()).orElseThrow();
            model.addAttribute("user", user);
        } else {
            return "redirect:/login";
        }

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
            // ★修正: Serviceが未読のみ返すので、そのまま使う
            List<TeacherReview> unreadGranted = usersService.getGrantedReviews(user.getUsersId());
            
            model.addAttribute("grantedCount", !unreadGranted.isEmpty() ? unreadGranted.size() : null);
            model.addAttribute("grantedDisclosures", unreadGranted);
        }

        // ニュース
        List<News> myNews = newsService.findNewsForUser(user);
        List<News> carouselNews = myNews.size() > 3 ? myNews.subList(0, 3) : myNews;
        model.addAttribute("carouselNews", carouselNews);

        // チャット通知
        List<ChatRoom> unreadGroups = chatRoomService.findUnreadGroupRooms(user.getUsersId());
        model.addAttribute("unreadGroups", unreadGroups);

        // DM通知
        List<DmDisplayDto> unreadDms = chatRoomService.findUnreadDmRooms(user.getUsersId());
        model.addAttribute("unreadDms", unreadDms);

        return "home";
    }
}