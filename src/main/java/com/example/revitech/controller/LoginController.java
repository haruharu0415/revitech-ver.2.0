
package com.example.revitech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.form.SignupForm; 

@Controller
public class LoginController {
//a
    @GetMapping("/login")
    public String showLogin() { 
        return "login";
    }
    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }
}