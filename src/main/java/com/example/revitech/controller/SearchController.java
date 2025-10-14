package com.example.revitech.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@Controller
public class SearchController {

    private final UsersService usersService;

    public SearchController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * ユーザー検索画面の表示と検索結果の処理
     * URL: /user-search
     */
    @GetMapping("/user-search")
    public String searchUsers(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Users> searchResults = null;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // サービスを呼び出して検索を実行
            searchResults = usersService.searchUsers(keyword.trim());
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("searchResults", searchResults);
        return "user-search"; // user-search.html に遷移
    }
}