package com.example.revitech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
    
    @GetMapping("/group")
    public String group() {
        return "group";
    }

    @GetMapping("/group-create")
    public String groupCreate() {
        return "group-create";
    }

    @GetMapping("/teacher-list")
    public String showTeacherList() {
        return "teacher-list";
    }
}