package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@Controller
public class DisclosureController {

    private final UsersService usersService;

    public DisclosureController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 開示請求一覧画面の表示
    @GetMapping("/disclosure/list")
    public String showDisclosureList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // ログインチェック
        if (userDetails == null) {
            return "redirect:/login";
        }

        // ログインユーザー情報の取得
        Users currentUser = usersService.findByNameOrEmail(userDetails.getUsername()).orElseThrow();
        
        // 権限チェック (先生か管理者のみ)
        if (currentUser.getRole() != 2 && currentUser.getRole() != 3) {
            return "redirect:/home"; // 権限がない場合はホームへ
        }

        // 開示請求リストを取得
        List<TeacherReview> list = usersService.getDisclosureList(currentUser);

        // 画面に渡すデータ
        model.addAttribute("disclosureList", list);
        model.addAttribute("user", currentUser);
        model.addAttribute("pageTitle", currentUser.getRole() == 3 ? "開示請求管理 (管理者)" : "開示請求一覧");

        return "disclosure-list"; // disclosure-list.html を表示
    }
}