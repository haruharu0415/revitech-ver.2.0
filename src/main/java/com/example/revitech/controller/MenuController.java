package com.example.revitech.controller;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@Controller
public class MenuController {

    private final UsersService usersService;

    // UsersServiceを利用できるようにコンストラクタで受け取る
    public MenuController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/option")
    public String option(Model model, @AuthenticationPrincipal User user) {
        // ログインしている場合、ユーザー情報を取得してModelに渡す
        if (user != null) {
            Optional<Users> currentUserOpt = usersService.findByEmail(user.getUsername());
            // "currentUser" という名前で画面に渡す (option.htmlで使用している名前と合わせる)
            currentUserOpt.ifPresent(users -> model.addAttribute("currentUser", users));
        }
        return "option";
    }
}