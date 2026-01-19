package com.example.revitech.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Subject;
import com.example.revitech.form.SignupForm;
import com.example.revitech.repository.SubjectRepository;
import com.example.revitech.service.UsersService;

import jakarta.validation.Valid;

@Controller
public class LoginController {

    private final UsersService usersService;
    private final SubjectRepository subjectRepository;

    public LoginController(UsersService usersService, SubjectRepository subjectRepository) {
        this.usersService = usersService;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/role-select")
    public String showRoleSelectPage() {
        return "role-select";
    }

    @PostMapping("/role-check")
    public String checkRolePassword(@RequestParam("role") int role,
                                    @RequestParam(name = "teacherPassword", required = false) String teacherPass,
                                    @RequestParam(name = "adminPassword", required = false) String adminPass,
                                    RedirectAttributes redirectAttributes) {

        if (role == 1) return "redirect:/signup?role=1";

        if (role == 2) {
            if ("teacher".equals(teacherPass)) return "redirect:/signup?role=2";
            redirectAttributes.addFlashAttribute("error", "先生用のパスワードが違います。");
            return "redirect:/role-select";
        }

        if (role == 3) {
            if ("gatikiti5".equals(adminPass)) return "redirect:/signup?role=3";
            redirectAttributes.addFlashAttribute("error", "管理者用のパスワードが違います。");
            return "redirect:/role-select";
        }
        return "redirect:/role-select";
    }

    @GetMapping("/signup")
    public String showSignupForm(@RequestParam("role") int role, Model model) {
        SignupForm form = new SignupForm();
        form.setRole(role);
        model.addAttribute("signupForm", form);

        // ★ 学科リストをDBから取得してドロップダウン用に渡す
        List<Subject> subjects = subjectRepository.findAll();
        model.addAttribute("subjects", subjects);

        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") @Valid SignupForm form,
                                BindingResult result,
                                Model model) {

        // パスワード一致チェック
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.signupForm", "パスワードが一致しません");
        }
        // メール重複チェック
        if (usersService.isEmailTaken(form.getEmail())) {
            result.rejectValue("email", "error.signupForm", "このメールアドレスは既に使用されています");
        }
        
        if (result.hasErrors()) {
            // エラーで入力画面に戻る際も、学科リストを再セットしないとセレクトボックスが空になります
            model.addAttribute("subjects", subjectRepository.findAll());
            return "signup";
        }
        
        // ★ UsersServiceのregisterメソッドを呼び出し、UsersとEnrollmentを保存
        usersService.register(form);

        // 成功したらログイン画面へ。クエリパラメータで成功通知を送ることも可能です
        return "redirect:/login?signup_success";
    }
}