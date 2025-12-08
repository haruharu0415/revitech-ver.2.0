package com.example.revitech.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.dto.TeacherListDto;
import com.example.revitech.service.UsersService;

@Controller
public class UserController {

    // サービスを正しく利用するためにインジェクションします
    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * ★★★ 以下、削除してしまっていたメソッドをすべて復活させます ★★★
     */
    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
    
   


    
    /**
     * ★★★ ここまでが復活させたメソッドです ★★★
     */

    /**
     * 教員一覧ページを表示します。
     * (このメソッドは前回から変更ありません)
     */
    @GetMapping("/teacher-list")
    public String showTeacherList(Model model) {
        List<TeacherListDto> teacherList = usersService.getTeacherListDetails();
        model.addAttribute("teacherList", teacherList);
        return "teacher-list";
    }
}