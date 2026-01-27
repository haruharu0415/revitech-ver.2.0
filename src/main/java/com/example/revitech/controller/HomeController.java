package com.example.revitech.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.entity.TeacherReview;
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
    private final ReviewService reviewService; // ★★★ 追加

    // コンストラクタに ReviewService を追加
    public HomeController(UsersService usersService, 
                          ChatRoomService chatRoomService, 
                          NewsService newsService,
                          ReviewService reviewService) {
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
        this.newsService = newsService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String usernameOrEmail = auth.getName();
            Users user = usersService.findByNameOrEmail(usernameOrEmail).orElse(null);
            
            if (user != null) {
                model.addAttribute("user", user);

                // 既存機能
                model.addAttribute("carouselNews", newsService.findTopNewsForUser(user, 5));
                model.addAttribute("unreadGroups", chatRoomService.findUnreadGroupRooms(user.getUsersId()));
                model.addAttribute("unreadDms", chatRoomService.findUnreadDmRooms(user.getUsersId()));

                // --- 管理者(Role=3) 通知処理 ---
                if (user.getRole() == 3) {
                    // 1. アカウント承認待ち
                    List<Users> pendingUsers = usersService.findPendingUsers();
                    if (!pendingUsers.isEmpty()) {
                        model.addAttribute("pendingCount", pendingUsers.size());
                    }
                    
                    // 2. ★★★ 追加: 開示請求の通知 ★★★
                    List<TeacherReview> disclosureRequests = reviewService.findPendingDisclosures();
                    if (!disclosureRequests.isEmpty()) {
                        model.addAttribute("disclosureRequests", disclosureRequests);
                        model.addAttribute("disclosureCount", disclosureRequests.size());
                    }
                }
                
                // --- 教員(Role=2) 通知処理 ---
                if (user.getRole() == 2) {
                    // 3. ★★★ 追加: 開示許可の通知 ★★★
                    List<TeacherReview> allGranted = reviewService.findUncheckedGrantedDisclosures(user.getUsersId());
                    
                    // まだ確認していない(TeacherChecked != 1)ものだけを抽出して通知する
                    List<TeacherReview> newGranted = allGranted.stream()
                        .filter(r -> r.getTeacherChecked() == null || r.getTeacherChecked() != 1)
                        .collect(Collectors.toList());

                    if (!newGranted.isEmpty()) {
                        model.addAttribute("grantedDisclosures", newGranted);
                        model.addAttribute("grantedCount", newGranted.size());
                    }
                }
            }
        }
        return "home";
    }
}