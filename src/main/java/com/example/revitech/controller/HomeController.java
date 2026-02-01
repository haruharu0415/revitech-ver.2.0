package com.example.revitech.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            // ★★★ 修正: UsersServiceから全件取得し、セッションを使ってフィルタリング ★★★
            List<TeacherReview> allGranted = usersService.getGrantedReviews(user.getUsersId());
            
            // セッションから「消去済みID」を取得して除外する
            Set<Integer> readIds = (Set<Integer>) session.getAttribute("readNotificationIds");
            if (readIds == null) readIds = new HashSet<>();
            
            final Set<Integer> finalReadIds = readIds; 
            List<TeacherReview> displayList = allGranted.stream()
                .filter(r -> !finalReadIds.contains(r.getReviewId()))
                .collect(Collectors.toList());

            model.addAttribute("grantedCount", !displayList.isEmpty() ? displayList.size() : null);
            model.addAttribute("grantedDisclosures", displayList);
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