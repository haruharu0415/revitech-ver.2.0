package com.example.revitech.controller;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Users;
import com.example.revitech.form.ProfileEditForm; // フォームクラスがない場合は作成が必要
import com.example.revitech.service.UsersService;

@Controller
public class ProfileController {

    private final UsersService usersService;

    public ProfileController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/profile/edit")
    public String showProfileEdit(Model model, @AuthenticationPrincipal User loginUser) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        
        // フォーム初期値設定
        ProfileEditForm form = new ProfileEditForm();
        form.setName(currentUser.getName());
        
        // ★修正: 先生の場合は自己紹介も取得してセット
        if (currentUser.getRole() == 2 || currentUser.getRole() == 3) {
            String intro = usersService.getTeacherIntroduction(currentUser.getUsersId());
            form.setIntroduction(intro);
        }

        model.addAttribute("profileEditForm", form);
        
        // 現在のアイコンを取得
        String currentIcon = usersService.getUserIconPath(currentUser.getUsersId());
        model.addAttribute("currentIcon", currentIcon);
        
        // 先生判定用フラグ
        model.addAttribute("isTeacher", (currentUser.getRole() == 2 || currentUser.getRole() == 3));

        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute ProfileEditForm form,
                                BindingResult result,
                                @RequestParam(value = "iconFile", required = false) MultipartFile iconFile,
                                @AuthenticationPrincipal User loginUser,
                                RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "profile-edit";
        }

        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();

        try {
            // ★修正: 自己紹介(form.getIntroduction())も渡す
            usersService.updateProfile(currentUser.getUsersId(), form.getName(), form.getIntroduction(), iconFile);
            redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました。");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "画像のアップロードに失敗しました。");
        }

        return "redirect:/option";
    }
}