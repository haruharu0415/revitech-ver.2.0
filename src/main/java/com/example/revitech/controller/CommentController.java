package com.example.revitech.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@Controller
public class CommentController {

    private final UsersService usersService;

    public CommentController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/review/{teacherId}")
    public String showReviewPage(@PathVariable("teacherId") Integer teacherId, Model model) {
        // ★★★ ここを修正 ★★★
        // findById から findUserOrDummyById に変更
        Optional<Users> teacherOpt = usersService.findById(teacherId);

        if (teacherOpt.isPresent()) {
            model.addAttribute("teacher", teacherOpt.get());
        } else {
            model.addAttribute("teacher", null);
        }
        
        return "review";
    }

    @GetMapping("/comment")
    public String showCommentPage(Model model) {
        return "comment";
    }
}