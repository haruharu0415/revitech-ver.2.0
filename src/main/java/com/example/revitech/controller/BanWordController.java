package com.example.revitech.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.BanWord;
import com.example.revitech.entity.Users;
import com.example.revitech.service.BanWordService;
import com.example.revitech.service.UsersService;

@Controller
@RequestMapping("/teacher/ban")
public class BanWordController {

    private final BanWordService banWordService;
    private final UsersService usersService;

    public BanWordController(BanWordService banWordService, UsersService usersService) {
        this.banWordService = banWordService;
        this.usersService = usersService;
    }

    // NGワード追加
    @PostMapping("/add")
    public String addWord(@RequestParam("teacherId") Integer teacherId,
                          @RequestParam("word") String word,
                          @AuthenticationPrincipal User loginUser,
                          RedirectAttributes redirectAttributes) {
        
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();

        // ★★★ 権限チェック: Role=2 (先生) かつ 本人であること ★★★
        if (currentUser.getRole() != 2 || !currentUser.getUsersId().equals(teacherId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "権限がありません。");
            return "redirect:/review/" + teacherId;
        }

        banWordService.addBanWord(teacherId, word);
        redirectAttributes.addFlashAttribute("successMessage", "NGワードを登録しました。");
        return "redirect:/review/" + teacherId;
    }

    // NGワード削除
    @PostMapping("/delete/{banId}")
    public String deleteWord(@PathVariable("banId") Integer banId,
                             @AuthenticationPrincipal User loginUser,
                             RedirectAttributes redirectAttributes) {
        
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        BanWord bw = banWordService.findById(banId);

        if (bw == null) {
            return "redirect:/home";
        }

        // ★★★ 権限チェック ★★★
        if (currentUser.getRole() != 2 || !currentUser.getUsersId().equals(bw.getTeacherId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "権限がありません。");
            return "redirect:/review/" + bw.getTeacherId();
        }

        banWordService.deleteBanWord(banId);
        redirectAttributes.addFlashAttribute("successMessage", "NGワードを削除しました。");
        return "redirect:/review/" + bw.getTeacherId();
    }
}