package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.revitech.entity.Users;
import com.example.revitech.form.SignupForm;
import com.example.revitech.service.UsersService;

import jakarta.validation.Valid;

@Controller
public class LoginController {

    private final UsersService usersService;

    @Autowired
    public LoginController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") @Valid SignupForm form,
                                BindingResult result,
                                Model model) {

        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", null, "パスワードが一致しません");
        }

        if (usersService.isEmailTaken(form.getUsername())) {
            result.rejectValue("username", null, "このユーザー名は既に使用されています");
        }

        if (result.hasErrors()) {
            return "signup";
        }

        Users user = new Users();
        user.setName(form.getUsername()); // username → name に変換
        user.setEmail(form.getUsername()); // email 兼 username 扱い（ログイン時に使う）
        user.setPassword(form.getPassword());
        user.setRole("USER"); // 必要に応じて変更可
        user.setStatus("active"); // 仮の状態設定
        
        usersService.save(user);

        return "redirect:/login";
    }
}