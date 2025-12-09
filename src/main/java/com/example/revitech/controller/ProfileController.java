package com.example.revitech.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.revitech.entity.Users;
import com.example.revitech.form.ProfileEditForm;
import com.example.revitech.service.UsersService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UsersService usersService;

    public ProfileController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 編集画面の表示
    @GetMapping("/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // ログインユーザーの取得
        Users user = usersService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // フォームの初期化
        ProfileEditForm form = new ProfileEditForm();
        form.setName(user.getName());
        
        model.addAttribute("profileEditForm", form);
        
        // 現在のアイコンパスを取得して画面に渡す
        String currentIcon = usersService.getUserIconPath(user.getUsersId());
        model.addAttribute("currentIcon", currentIcon);
        
        return "profile-edit";
    }

    // 更新処理
    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute("profileEditForm") @Valid ProfileEditForm form,
                                BindingResult result,
                                Model model) {
        
        Users user = usersService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 入力エラーがある場合
        if (result.hasErrors()) {
             model.addAttribute("currentIcon", usersService.getUserIconPath(user.getUsersId()));
            return "profile-edit";
        }

        try {
            // サービスで更新処理を実行
            usersService.updateProfile(user.getUsersId(), form.getName(), form.getIconFile());
        } catch (Exception e) {
            e.printStackTrace();
            // 例外発生時
            model.addAttribute("error", "プロフィールの更新に失敗しました: " + e.getMessage());
            model.addAttribute("currentIcon", usersService.getUserIconPath(user.getUsersId()));
            return "profile-edit";
        }

        // 更新成功時はオプション画面へリダイレクト
        return "redirect:/option"; 
    }
}