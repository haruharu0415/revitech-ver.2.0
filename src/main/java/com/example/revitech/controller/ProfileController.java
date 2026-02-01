package com.example.revitech.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam; // 追加

import com.example.revitech.entity.Users;
import com.example.revitech.form.ProfileEditForm;
import com.example.revitech.repository.SubjectRepository;
import com.example.revitech.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class ProfileController {

    private final UsersService usersService;
    private final SubjectRepository subjectRepository;

    public ProfileController(UsersService usersService, SubjectRepository subjectRepository) {
        this.usersService = usersService;
        this.subjectRepository = subjectRepository;
    }

    // --- プロフィール編集画面の表示 ---
    @GetMapping("/profile/edit")
    public String showProfileEdit(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Users user = usersService.findByNameOrEmail(userDetails.getUsername()).orElseThrow();
        
        ProfileEditForm form = new ProfileEditForm();
        form.setName(user.getName());
        
        boolean isTeacher = (user.getRole() == 2 || user.getRole() == 3);
        model.addAttribute("isTeacher", isTeacher);
        
        if (isTeacher) {
            String intro = usersService.getTeacherIntroduction(user.getUsersId());
            form.setIntroduction(intro);
            
            // 現在の担当科目(ハッシュタグ由来)を取得してフォームにセット
            List<Integer> currentSubjectIds = usersService.getTeacherSubjectIds(user.getUsersId());
            form.setTeacherSubjectIds(currentSubjectIds);
            
            // 学科一覧を画面に渡す
            model.addAttribute("subjects", subjectRepository.findAll());
        }

        model.addAttribute("profileEditForm", form);
        model.addAttribute("currentIcon", usersService.getUserIconPath(user.getUsersId()));
        
        return "profile-edit";
    }

    // --- プロフィール更新処理 ---
    @PostMapping("/profile/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute @Valid ProfileEditForm form,
                                BindingResult result,
                                Model model) {
        Users user = usersService.findByNameOrEmail(userDetails.getUsername()).orElseThrow();
        
        if (result.hasErrors()) {
            boolean isTeacher = (user.getRole() == 2 || user.getRole() == 3);
            model.addAttribute("isTeacher", isTeacher);
            model.addAttribute("currentIcon", usersService.getUserIconPath(user.getUsersId()));
            if (isTeacher) {
                model.addAttribute("subjects", subjectRepository.findAll());
            }
            return "profile-edit";
        }

        try {
            usersService.updateProfile(
                user.getUsersId(),
                form.getName(),
                form.getIntroduction(),
                form.getIconFile(),
                form.getTeacherSubjectIds()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/profile/edit?error";
        }

        return "redirect:/option";
    }

    // --- ハッシュタグ削除機能 (GET/POST両対応) ---
    @RequestMapping(value = "/teacher/hashtag/delete/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteHashtag(@PathVariable("id") Integer id, HttpServletRequest request) {
        usersService.deleteHashtag(id);
        
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }

    // ★★★ 追加: ハッシュタグ追加機能 ★★★
    @PostMapping("/teacher/hashtag/add")
    public String addHashtag(@RequestParam("hashtag") String hashtag,
                             @RequestParam(name="teacherId", required=false) Integer formTeacherId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletRequest request) {
        
        // ログインユーザーを取得
        Users currentUser = usersService.findByNameOrEmail(userDetails.getUsername()).orElseThrow();
        
        // 追加対象の先生IDを決定 (フォームから送られてこなければ、ログインユーザー自身とする)
        Integer targetTeacherId = (formTeacherId != null) ? formTeacherId : currentUser.getUsersId();

        // 権限チェック: 自分自身 または 管理者(Role=3) のみ追加可能
        if (currentUser.getUsersId().equals(targetTeacherId) || currentUser.getRole() == 3) {
             usersService.addHashtag(targetTeacherId, hashtag);
        }
        
        // 元のページに戻る
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }
}