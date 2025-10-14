package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.service.UsersService;

@Controller
public class UserController {

    private final UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

   /* @GetMapping("/home")
    public String showHome(Model model) {
        var users = usersService.findAll();
        model.addAttribute("users", users);
        return "home";
    }*/

    @GetMapping("/teacher-list")
    public String userList() {
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
}