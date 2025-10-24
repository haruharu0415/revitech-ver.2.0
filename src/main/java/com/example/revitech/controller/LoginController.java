package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ★ Model をインポート
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Users;
import com.example.revitech.form.SignupForm; // ★ SignupForm を使用
import com.example.revitech.service.UsersService;

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

    // ★★★ ここを再確認 ★★★
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        // ↓↓↓ この行が絶対に必要！ ↓↓↓
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }
    // ★★★ ここまで ★★★

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") @Valid SignupForm form,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // --- バリデーション (変更なし) ---
        if (form.getPassword() != null && !form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.signupForm", "パスワードが一致しません");
        }
        if (form.getEmail() != null && !form.getEmail().endsWith("@jec.ac.jp")) {
             result.rejectValue("email", "error.signupForm", "メールアドレスは @jec.ac.jp で終わる必要があります。");
        }
        if (form.getEmail() != null && !form.getEmail().isEmpty() && usersService.isEmailTaken(form.getEmail())) {
            result.rejectValue("email", "error.signupForm", "このメールアドレスは既に使用されています");
        }
        // (役割削除済み)

        if (result.hasErrors()) {
            return "signup";
        }

        // --- ユーザーエンティティ作成 (変更なし) ---
        Users user = new Users();
        user.setName(form.getName()); // SignupForm の getName()
        user.setEmail(form.getEmail()); // SignupForm の getEmail()
        user.setPassword(passwordEncoder.encode(form.getPassword())); // SignupForm の getPassword()
        user.setRole(3); // デフォルト役割 (STUDENT)
        user.setStatus("active");

        // --- ユーザー保存 (変更なし) ---
        try {
            usersService.save(user);
            redirectAttributes.addFlashAttribute("signupSuccess", "ユーザー登録が完了しました。ログインしてください。");
            return "redirect:/login";
        } catch (Exception e) {
            System.err.println("ユーザー保存中にエラー発生: " + e.getMessage());
            model.addAttribute("signupError", "ユーザー登録中にエラーが発生しました。");
            return "signup";
        }
    }
}