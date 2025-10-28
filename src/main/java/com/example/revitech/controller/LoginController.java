package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ★ インポート確認
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Users;
import com.example.revitech.form.SignupForm; // ★ SignupForm を使用
import com.example.revitech.service.UsersService; // ★ UsersService を使用

import jakarta.validation.Valid;

@Controller
public class LoginController {

    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(UsersService usersService, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        // (th:object="${signupForm}" のために空のフォームを渡す)
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") @Valid SignupForm form,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // --- バリデーション ---
        if (form.getPassword() != null && !form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.signupForm", "パスワードが一致しません");
        }
        if (form.getEmail() != null && !form.getEmail().endsWith("@jec.ac.jp")) {
             result.rejectValue("email", "error.signupForm", "メールアドレスは @jec.ac.jp で終わる必要があります。");
        }
        
        // (メール重複チェック)
        if (form.getEmail() != null && !form.getEmail().isEmpty() && usersService.isEmailTaken(form.getEmail())) {
            result.rejectValue("email", "error.signupForm", "このメールアドレスは既に使用されています");
        }

        // ★★★ ここに name の重複チェックを追加 ★★★
        if (form.getName() != null && !form.getName().isEmpty() && usersService.isNameTaken(form.getName())) {
            // (name フィールドに対してエラーを紐付け)
            result.rejectValue("name", "error.signupForm", "このユーザー名は既に使用されています");
        }
        // ★★★ 追加ここまで ★★★

        if (result.hasErrors()) {
            return "signup"; // エラーがあれば signup ページに戻る
        }

        // --- ユーザーエンティティ作成 ---
        Users user = new Users();
        user.setName(form.getName()); 
        user.setEmail(form.getEmail()); 
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(3); // デフォルト役割 (STUDENT)
        user.setStatus("active");

        // --- ユーザー保存 ---
        try {
            usersService.save(user);
            redirectAttributes.addFlashAttribute("signupSuccess", "ユーザー登録が完了しました。ログインしてください。");
            return "redirect:/login";
        } catch (Exception e) {
            // (DB側の UNIQUE 制約エラーなどもここでキャッチ)
            System.err.println("ユーザー保存中にエラー発生: " + e.getMessage());
            model.addAttribute("signupError", "ユーザー登録中にエラーが発生しました。");
            // (エラー内容を特定して表示するのも良い)
            if (e.getMessage().contains("UQ_Users_Name") || e.getMessage().contains("name")) {
                 model.addAttribute("signupError", "そのユーザー名は既に使用されています。");
            } else if (e.getMessage().contains("UQ_Users_Email") || e.getMessage().contains("email")) {
                 model.addAttribute("signupError", "そのメールアドレスは既に使用されています。");
            }
            
            return "signup";
        }
    }
}