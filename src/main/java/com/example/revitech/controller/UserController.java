package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.dto.TeacherListDto;
import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    // ★ 修正: 検索キーワード (keyword) を受け取る
    @GetMapping("/teacher-list")
    public String showTeacherList(@RequestParam(name = "keyword", required = false) String keyword,
                                  Model model, 
                                  @AuthenticationPrincipal User loginUser) {
        
        // 検索ワードを渡してリスト取得
        List<TeacherListDto> teachers = usersService.getTeacherListDetails(keyword);
        
        model.addAttribute("teachers", teachers);
        model.addAttribute("keyword", keyword); // 検索バーに値を保持させるため
        
        if (loginUser != null) {
            Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElse(null);
            model.addAttribute("user", currentUser);
        }
        
        return "teacher-list";
    }

    // アカウント削除確認
    @GetMapping("/account/delete")
    public String showDeleteConfirmation(Model model, @AuthenticationPrincipal User loginUser) {
        if (loginUser == null) return "redirect:/login";
        return "account-delete";
    }

    // アカウント削除実行
    @PostMapping("/account/delete")
    public String deleteMyAccount(@AuthenticationPrincipal User loginUser, 
                                  HttpServletRequest request, 
                                  RedirectAttributes redirectAttributes) {
        if (loginUser == null) return "redirect:/login";
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        usersService.softDeleteUser(currentUser.getUsersId());
        try { request.logout(); } catch (ServletException e) { e.printStackTrace(); }
        return "redirect:/login?logout"; 
    }

    // 管理者削除
    @PostMapping("/admin/delete/{userId}")
    public String deleteUserByAdmin(@PathVariable Integer userId, 
                                    @AuthenticationPrincipal User loginUser,
                                    RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() == 3) {
            usersService.softDeleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "ユーザーを削除しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "権限がありません。");
        }
        return "redirect:/teacher-list";
    }
}