package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.TeacherHashtag;
import com.example.revitech.entity.TeacherImage;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.Users;
import com.example.revitech.service.TeacherProfileService;
import com.example.revitech.service.UsersService;

@Controller
@RequestMapping("/teacher/profile")
public class TeacherProfileController {

    private final TeacherProfileService profileService;
    private final UsersService usersService;

    public TeacherProfileController(TeacherProfileService profileService, UsersService usersService) {
        this.profileService = profileService;
        this.usersService = usersService;
    }

    // 編集画面表示
    @GetMapping("/edit")
    public String editProfile(Model model, @AuthenticationPrincipal User loginUser) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2) return "redirect:/home";

        TeacherProfile profile = profileService.getProfile(currentUser.getUsersId());
        List<TeacherImage> images = profileService.getImages(currentUser.getUsersId());
        // ★ タグ一覧を取得
        List<TeacherHashtag> hashtags = profileService.getHashtags(currentUser.getUsersId());

        model.addAttribute("profile", profile);
        model.addAttribute("images", images);
        model.addAttribute("hashtags", hashtags); // 画面へ渡す
        model.addAttribute("user", currentUser);

        return "teacher-profile-edit";
    }

    
    
    // プロフィール更新
    @PostMapping("/update")
    public String updateProfile(@RequestParam("introduction") String introduction,
                                @RequestParam(value = "iconFile", required = false) MultipartFile iconFile,
                                @AuthenticationPrincipal User loginUser,
                                RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2) return "redirect:/home";

        profileService.saveProfile(currentUser.getUsersId(), introduction, iconFile);
        redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました。");
        return "redirect:/teacher/profile/edit";
    }

    // アイコンのみ更新
    @PostMapping("/update-icon")
    public String updateIconOnly(@RequestParam("iconFile") MultipartFile iconFile,
                                 @AuthenticationPrincipal User loginUser,
                                 RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2) return "redirect:/home";

        profileService.updateIconOnly(currentUser.getUsersId(), iconFile);
        redirectAttributes.addFlashAttribute("successMessage", "アイコンを変更しました。");
        return "redirect:/review/" + currentUser.getUsersId();
    }

    // ★ 追加: ハッシュタグ追加
    @PostMapping("/add-hashtag")
    public String addHashtag(@RequestParam("hashtag") String hashtag,
                             @AuthenticationPrincipal User loginUser,
                             RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2) return "redirect:/home";

        profileService.addHashtag(currentUser.getUsersId(), hashtag);
        redirectAttributes.addFlashAttribute("successMessage", "ハッシュタグを追加しました。");
        return "redirect:/teacher/profile/edit";
    }

    // ★ 追加: ハッシュタグ削除
    @PostMapping("/delete-hashtag/{hashtagId}")
    public String deleteHashtag(@PathVariable Integer hashtagId,
                                @AuthenticationPrincipal User loginUser,
                                RedirectAttributes redirectAttributes) {
        // 本人チェックは省略（簡易実装）
        profileService.deleteHashtag(hashtagId);
        redirectAttributes.addFlashAttribute("successMessage", "ハッシュタグを削除しました。");
        return "redirect:/teacher/profile/edit";
    }

    // カルーセル画像追加
    @PostMapping("/add-images")
    public String addImages(@RequestParam("imageFiles") MultipartFile[] imageFiles,
                            @AuthenticationPrincipal User loginUser,
                            RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2) return "redirect:/home";

        profileService.addCarouselImages(currentUser.getUsersId(), imageFiles);
        redirectAttributes.addFlashAttribute("successMessage", "画像を追加しました。");
        return "redirect:/teacher/profile/edit";
    }

    // 画像削除
    @PostMapping("/delete-image/{imageId}")
    public String deleteImage(@PathVariable Integer imageId,
                              @AuthenticationPrincipal User loginUser,
                              RedirectAttributes redirectAttributes) {
        profileService.deleteImage(imageId);
        redirectAttributes.addFlashAttribute("successMessage", "画像を削除しました。");
        return "redirect:/teacher/profile/edit";
    }
}