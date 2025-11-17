package com.example.revitech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Users;
import com.example.revitech.form.SignupForm;
import com.example.revitech.service.UsersService;

import jakarta.validation.Valid;

@Controller
public class LoginController {

    private final UsersService usersService;

    public LoginController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * ★★★ 新規追加 ★★★
     * 役割選択ページを表示します。
     * login.htmlの「新規アカウント作成」ボタンは、/signupではなく、この/role-selectにリンクさせます。
     */
    @GetMapping("/role-select")
    public String showRoleSelectPage() {
        return "role-select";
    }

    /**
     * ★★★ 新規追加 ★★★
     * 役割選択ページからのPOSTリクエストを処理し、パスワードを検証します。
     */
    @PostMapping("/role-check")
    public String checkRolePassword(@RequestParam("role") int role,
                                    @RequestParam(name = "teacherPassword", required = false) String teacherPass,
                                    @RequestParam(name = "adminPassword", required = false) String adminPass,
                                    RedirectAttributes redirectAttributes) {

        if (role == 1) { // 生徒
            // パスワードチェックは不要。そのままサインアップページへ
            return "redirect:/signup?role=1";
        }

        if (role == 2) { // 先生
            if ("teacher".equals(teacherPass)) {
                // パスワード一致。サインアップページへ
                return "redirect:/signup?role=2";
            } else {
                // パスワード不一致。エラーメッセージを渡して選択画面に戻る
                redirectAttributes.addFlashAttribute("error", "先生用のパスワードが違います。");
                return "redirect:/role-select";
            }
        }

        if (role == 3) { // 管理者
            if ("gatikiti5".equals(adminPass)) {
                // パスワード一致。サインアップページへ
                return "redirect:/signup?role=3";
            } else {
                // パスワード不一致。エラーメッセージを渡して選択画面に戻る
                redirectAttributes.addFlashAttribute("error", "管理者用のパスワードが違います。");
                return "redirect:/role-select";
            }
        }
        
        // 通常はここに来ないが、念のため
        return "redirect:/role-select";
    }


    /**
     * ★★★ 修正 ★★★
     * サインアップページを表示します。
     * URLのパラメータから「role」を受け取り、フォームに設定します。
     */
    @GetMapping("/signup")
    public String showSignupForm(@RequestParam("role") int role, Model model) {
        SignupForm form = new SignupForm();
        form.setRole(role); // 役割IDをフォームにセット
        model.addAttribute("signupForm", form);
        return "signup";
    }

    /**
     * ★★★ 修正 ★★★
     * サインアップ処理を実行します。
     * フォームから渡された「role」を使って、正しい役割でユーザーを登録します。
     */
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") @Valid SignupForm form,
                                BindingResult result) {

        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.signupForm", "パスワードが一致しません");
        }
        if (usersService.isEmailTaken(form.getEmail())) {
            result.rejectValue("email", "error.signupForm", "このメールアドレスは既に使用されています");
        }
        if (result.hasErrors()) {
            return "signup";
        }
        Users user = new Users();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        
        // ★ 修正: 以前は「1」で固定していたが、フォームから渡された役割IDを設定する
        user.setRole(form.getRole()); 
        
        user.setStatus("active");
        usersService.save(user);

        return "redirect:/login";
    }
}