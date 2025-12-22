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
        if (userDetails != null) {
            Users user = usersService.findByEmail(userDetails.getUsername()).orElse(null);
            
            if (user != null) {
                // ★★★ 修正箇所：userオブジェクトとiconUrlを両方渡す ★★★
                model.addAttribute("user", user);
                String iconUrl = usersService.getUserIconPath(user.getUsersId());
                model.addAttribute("iconUrl", iconUrl);
            }
        }
        return "option";
    }
}