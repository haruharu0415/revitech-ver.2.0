package com.example.revitech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.service.UsersService;

@Controller
@RequestMapping("/admin")
public class AdminApprovalController {

    private final UsersService usersService;

    public AdminApprovalController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 承認待ちユーザー一覧画面を表示
    @GetMapping("/approvals")
    public String listPendingUsers(Model model) {
        // サービスから承認待ちユーザーのリストを取得して画面に渡す
        model.addAttribute("pendingUsers", usersService.findPendingUsers());
        return "admin/approval-list"; // templates/admin/approval-list.html を表示
    }

    // 承認ボタンが押されたときの処理
    @PostMapping("/approve")
    public String approveUser(@RequestParam("userId") Integer userId) {
        // ユーザーを承認(active)にする
        usersService.approveUser(userId);
        // 一覧画面に戻る
        return "redirect:/admin/approvals";
    }
}