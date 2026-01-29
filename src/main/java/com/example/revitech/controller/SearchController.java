package com.example.revitech.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@Controller
public class SearchController {

    private final UsersService usersService;

    public SearchController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/user-search")
    public String searchUsers(@RequestParam(name = "keyword", required = false) String keyword, 
                              Model model,
                              @AuthenticationPrincipal User loginUser) { // ★ ログインユーザー情報を取得
        
        List<UserSearchDto> searchResults = Collections.emptyList();

        // ログイン中のユーザー情報をDBから取得
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();

        // キーワードがあれば検索実行
        if (keyword != null && !keyword.trim().isEmpty()) {
            // ★ 第二引数に「自分のID」を渡して、検索結果から除外してもらう
            searchResults = usersService.searchUsers(keyword, currentUser.getUsersId());
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("searchResults", searchResults);
        
        return "user-search"; // templates/user-search.html を表示
    }
}