// UserController.java の全文
package com.example.revitech.controller;

import java.util.List; // 【追加】

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 【追加】
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.entity.Users; // 【追加】
import com.example.revitech.service.UsersService;

@Controller
public class UserController {

    private final UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    // ▼▼▼【このメソッド全体を修正】▼▼▼
    @GetMapping("/teacher-list")
    public String userList(Model model) { // Modelを引数に追加
        // 1. UsersServiceを使って教員のリストを取得
        List<Users> teachers = usersService.findTeachers();
        
        // 2. 取得したリストを "teachers" という名前でModelに追加
        model.addAttribute("teachers", teachers);
        
        // 3. テンプレート名を返す
        return "teacher-list"; 
    }

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
}