package com.example.revitech.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.entity.Users;
import com.example.revitech.service.ChatRoomService;
import com.example.revitech.service.NewsService;
import com.example.revitech.service.UsersService;

@Controller
public class HomeController {

    private final UsersService usersService;
    private final ChatRoomService chatRoomService;
    private final NewsService newsService;

    public HomeController(UsersService usersService, ChatRoomService chatRoomService, NewsService newsService) {
        this.usersService = usersService;
        this.chatRoomService = chatRoomService;
        this.newsService = newsService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String email = auth.getName();
            Users user = usersService.findByEmail(email).orElse(null);
            
            if (user != null) {
                model.addAttribute("user", user);

                // 1. お知らせ (最新5件をカルーセル用)
                model.addAttribute("carouselNews", newsService.findTopNewsForUser(user, 5));

                // 2. 未読グループ
                model.addAttribute("unreadGroups", chatRoomService.findUnreadGroupRooms(user.getUsersId()));

                // 3. 未読DM
                model.addAttribute("unreadDms", chatRoomService.findUnreadDmRooms(user.getUsersId()));

                // ※開示請求などのデータは、該当Serviceがあればここでセットしてください
                // model.addAttribute("disclosureRequests", ...);
                // model.addAttribute("grantedDisclosures", ...);
            }
        }
        return "home";
    }
}