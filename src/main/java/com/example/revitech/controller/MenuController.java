package com.example.revitech.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@Controller
public class MenuController {

    private final UsersService usersService;

    public MenuController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/option")
    public String option(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 未ログイン時はログイン画面へ
        if (userDetails == null) {
            return "redirect:/login";
        }

        // ユーザー情報を取得
        Users user = usersService.findByNameOrEmail(userDetails.getUsername()).orElse(null);
        
        if (user != null) {
            model.addAttribute("user", user);
            String iconUrl = usersService.getUserIconPath(user.getUsersId());
            model.addAttribute("iconUrl", iconUrl);

            // ★★★ 追加: 学生(Role=1)なら学科名を取得して画面に渡す ★★★
            if (user.getRole() == 1) {
                String subjectName = usersService.getStudentSubjectName(user.getUsersId());
                model.addAttribute("subjectName", subjectName);
            }
            
            return "option";
        } else {
            return "redirect:/login?error";
        }
    }
}