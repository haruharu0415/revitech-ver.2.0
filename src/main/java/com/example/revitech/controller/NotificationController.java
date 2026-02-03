package com.example.revitech.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class NotificationController {

    private final UsersService usersService;

    public NotificationController(UsersService usersService) {
        this.usersService = usersService;
    }

    // ★修正: @PostMapping から @RequestMapping に変更し、GETでもPOSTでも動くようにする
    // HTMLが <a href="..."> (GET) でも <form method="post"> (POST) でも対応可能になります
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/notification/clear", method = {RequestMethod.GET, RequestMethod.POST})
    public String clearNotifications(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        if (userDetails != null) {
            // ★修正: HomeControllerと合わせて findByEmail を使用 (安定性向上)
            Users user = usersService.findByEmail(userDetails.getUsername()).orElse(null);
            
            if (user != null && user.getRole() == 2) {
                // 現在の通知一覧を取得
                List<TeacherReview> list = usersService.getGrantedReviews(user.getUsersId());
                
                // セッションから「消去済みIDセット」を取得
                Set<Integer> readIds = (Set<Integer>) session.getAttribute("readNotificationIds");
                if (readIds == null) {
                    readIds = new HashSet<>();
                }
                
                // 全ての通知IDを「消去済み」としてセッションに追加
                for (TeacherReview review : list) {
                    readIds.add(review.getReviewId());
                }
                
                // セッションに保存
                session.setAttribute("readNotificationIds", readIds);
            }
        }
        return "redirect:/home";
    }
}