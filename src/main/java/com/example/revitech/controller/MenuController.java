package com.example.revitech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/")
    public String index() {
        // ★★★ "home.html" を表示するように変更 ★★★
        return "home";
    }

    @GetMapping("/option")
    public String option() {
        // "option.html" を表示する
        return "option";
    }
}

