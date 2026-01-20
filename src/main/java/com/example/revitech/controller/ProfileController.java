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
import com.example.revitech.form.ProfileEditForm;
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
        
        // 先生の場合は自己紹介も取得してセット
        if (currentUser.getRole() == 2 || currentUser.getRole() == 3) {
            String intro = usersService.getTeacherIntroduction(currentUser.getUsersId());
            form.setIntroduction(intro);
        }
        // 生徒の場合はStudentProfileから自己紹介を取得する必要があるが、
        // 今回の追加要件である「学科名」の処理に集中するため、ここでは省略または既存の実装に従う
        // もし生徒の自己紹介も表示するなら usersService.getStudentIntroduction(...) のようなメソッドが必要

        model.addAttribute("profileEditForm", form);
        
        // 現在のアイコンを取得
        String currentIcon = usersService.getUserIconPath(currentUser.getUsersId());
        model.addAttribute("currentIcon", currentIcon);
        
        // 先生判定用フラグ
        boolean isTeacher = (currentUser.getRole() == 2 || currentUser.getRole() == 3);
        model.addAttribute("isTeacher", isTeacher);

        // ★★★ 追加: 生徒の場合、学科名を取得して渡す ★★★
        if (!isTeacher) { // 生徒の場合
            String subjectName = usersService.getStudentSubjectName(currentUser.getUsersId());
            model.addAttribute("subjectName", subjectName);
        }

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
            usersService.updateProfile(currentUser.getUsersId(), form.getName(), form.getIntroduction(), iconFile);
            redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました。");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "画像のアップロードに失敗しました。");
        }

        return "redirect:/option";
    }
}