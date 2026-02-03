package com.example.revitech.controller;

import java.util.List;

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

    // GETとPOST両方対応
    @RequestMapping(value = "/notification/clear", method = {RequestMethod.GET, RequestMethod.POST})
    public String clearNotifications(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        if (userDetails != null) {
            Users user = usersService.findByEmail(userDetails.getUsername()).orElse(null);
            
            if (user != null && user.getRole() == 2) {
                // 1. 未読リストを取得
                List<TeacherReview> unreadList = usersService.getGrantedReviews(user.getUsersId());
                
                // 2. DB上で「既読」に更新 (これで永続的に消える)
                if (!unreadList.isEmpty()) {
                    usersService.markReviewsAsChecked(unreadList);
                }
                
                // セッション削除
                session.removeAttribute("readNotificationIds");
            }
        }
        return "redirect:/home";
    }
}