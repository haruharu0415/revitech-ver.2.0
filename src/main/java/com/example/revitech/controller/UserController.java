package com.example.revitech.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.dto.TeacherListDto;
import com.example.revitech.service.UsersService;

@Controller
public class UserController {

    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * 教員一覧表示
     */
    @GetMapping("/teacher-list")
    public String showTeacherList(Model model) {
        List<TeacherListDto> teachers = usersService.getTeacherListDetails();
        model.addAttribute("teachers", teachers);
        return "teacher-list";
    }
}