package com.example.revitech.controller;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.BanWord;
import com.example.revitech.entity.TeacherHashtag;
import com.example.revitech.entity.Users;
import com.example.revitech.form.ProfileEditForm;
import com.example.revitech.service.UsersService;

@Controller
public class ProfileController {

    private final UsersService usersService;

    public ProfileController(UsersService usersService) {
        this.usersService = usersService;
    }

    // プロフィール編集画面
    @GetMapping("/profile/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        Users user = usersService.findByNameOrEmail(userDetails.getUsername()).orElseThrow();
        ProfileEditForm form = new ProfileEditForm();
        form.setName(user.getName());
        if (user.getRole() == 2 || user.getRole() == 3) {
            form.setIntroduction(usersService.getTeacherIntroduction(user.getUsersId()));
        }
        model.addAttribute("profileEditForm", form);
        model.addAttribute("user", user);
        model.addAttribute("currentIcon", usersService.getUserIconPath(user.getUsersId()));
        model.addAttribute("isTeacher", (user.getRole() == 2 || user.getRole() == 3));
        return "profile-edit";
    }

    // プロフィール更新
    @PostMapping("/profile/edit")
    public String updateProfile(@Validated ProfileEditForm form, BindingResult result,
                                @AuthenticationPrincipal UserDetails userDetails, Model model) throws IOException {
        if (userDetails == null) return "redirect:/login";
        Users user = usersService.findByNameOrEmail(userDetails.getUsername()).orElseThrow();
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("currentIcon", usersService.getUserIconPath(user.getUsersId()));
            model.addAttribute("isTeacher", (user.getRole() == 2 || user.getRole() == 3));
            return "profile-edit";
        }
        usersService.updateProfile(user.getUsersId(), form.getName(), form.getIntroduction(), form.getIconFile());
        if (user.getRole() == 2) return "redirect:/review/" + user.getUsersId();
        return "redirect:/home";
    }

    // ハッシュタグ追加
    @PostMapping("/teacher/hashtag/add")
    public String addHashtag(@RequestParam("teacherId") Integer teacherId, @RequestParam("hashtag") String hashtag) {
        usersService.addHashtag(teacherId, hashtag);
        return "redirect:/review/" + teacherId;
    }

    // ハッシュタグ削除
    @PostMapping("/teacher/hashtag/delete/{id}")
    public String deleteHashtag(@PathVariable("id") Integer id) {
        TeacherHashtag tag = usersService.findHashtagById(id).orElse(null);
        if (tag != null) {
            Integer teacherId = tag.getTeacherId();
            usersService.deleteHashtag(id);
            return "redirect:/review/" + teacherId;
        }
        return "redirect:/home";
    }

    // ★★★ 修正: NGワード追加 (URLを変更して既存コントローラーとの競合を回避) ★★★
    @PostMapping("/teacher/ban/register") 
    public String addBanWord(@RequestParam("teacherId") Integer teacherId, @RequestParam("word") String word) {
        usersService.addBanWord(teacherId, word);
        return "redirect:/review/" + teacherId;
    }

    // ★★★ 修正: NGワード削除 (URLを変更) ★★★
    @PostMapping("/teacher/ban/remove/{id}")
    public String deleteBanWord(@PathVariable("id") Integer id) {
        BanWord bw = usersService.findBanWordById(id).orElse(null);
        if (bw != null) {
            Integer teacherId = bw.getTeacherId();
            usersService.deleteBanWord(id);
            return "redirect:/review/" + teacherId;
        }
        return "redirect:/home";
    }
}