// LoginController.java の全文
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

    // ▼▼▼【修正箇所】processSignupメソッド全体を修正 ▼▼▼
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") @Valid SignupForm form,
                                BindingResult result,
                                Model model) {

        // パスワードの一致チェック
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", null, "パスワードが一致しません");
        }

        // メールアドレスの重複チェック
        if (usersService.isEmailTaken(form.getEmail())) {
            result.rejectValue("email", null, "このメールアドレスは既に使用されています");
        }

        if (result.hasErrors()) {
            return "signup";
        }

        // データベースに保存するユーザー情報を作成
        Users user = new Users();
        user.setName(form.getName());     // フォームの name をセット
        user.setEmail(form.getEmail());   // フォームの email をセット
        user.setPassword(form.getPassword());
        user.setRole("USER"); 
        user.setStatus("active");
        
        usersService.save(user);

        return "redirect:/login";
    }
}