package com.example.revitech.controller;

import java.util.Optional;
// import java.util.UUID; // ★ UUID は使わない

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.Users; // ★ id は Long
import com.example.revitech.service.UsersService; // ★ findById(Long)

@Controller
public class CommentController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/comment")
    // ★ teacherId の型を Long に戻す ★
    public String showCommentPage(@RequestParam("teacherId") Long teacherId,
                                  Model model) {

        // ★ usersService.findById は Long を受け取る ★
        Optional<Users> teacherOpt = usersService.findById(teacherId);

        if (teacherOpt.isPresent() && teacherOpt.get().getRole() != null && teacherOpt.get().getRole() == 2) {
            Users teacher = teacherOpt.get();
            model.addAttribute("teacher", teacher); // Users (id は Long)

            // ★ commentService.getCommentsForTeacher(Long) を呼び出す (仮) ★
            // List<CommentDto> comments = commentService.getCommentsForTeacher(teacherId);
            // model.addAttribute("comments", comments);

        } else {
            return "redirect:/teacher-list?error=TeacherNotFound";
        }

        return "comment";
    }
}