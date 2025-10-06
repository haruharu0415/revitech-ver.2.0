// LoginController.java
package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.revitech.entity.Teacher;
import com.example.revitech.form.SignupForm;
import com.example.revitech.service.TeacherService;

import jakarta.validation.Valid;

@Controller
public class LoginController {

    private final TeacherService teacherService;

    @Autowired
    public LoginController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("signupForm") @Valid SignupForm form,
                                BindingResult bindingResult,
                                Model model) {

        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", null, "パスワードが一致しません");
        }

        if (teacherService.isUsernameTaken(form.getUsername())) {
            bindingResult.rejectValue("username", null, "このユーザー名はすでに使われています");
        }

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        Teacher teacher = new Teacher();
        teacher.setUsername(form.getUsername());
        teacher.setPassword(form.getPassword());
        teacher.setStatus("active");
        teacher.setRole("USER");

        teacherService.save(teacher);

        return "redirect:/home";
    }
}
