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

            // ★★★ 修正: Roleごとに「学科/科目」の情報を取得してViewに渡す ★★★
            if (user.getRole() == 1) { // 生徒の場合
                String subjectName = usersService.getStudentSubjectName(user.getUsersId());
                model.addAttribute("subjectLabel", "所属学科");
                model.addAttribute("subjectInfo", subjectName);
            } 
            else if (user.getRole() == 2) { // 先生の場合
                String subjectNames = usersService.getTeacherSubjectNames(user.getUsersId());
                model.addAttribute("subjectLabel", "担当科目");
                model.addAttribute("subjectInfo", subjectNames);
            }
            // 管理者(Role=3)は subjectInfo をセットしないので表示されない
            
            return "option";
        } else {
            return "redirect:/login?error";
        }
    }
}