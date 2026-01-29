package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/users")
public class UsersApiController {

    private final UsersService usersService;

    public UsersApiController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/search")
    public List<UserSearchDto> searchUsers(@RequestParam String query, @AuthenticationPrincipal User loginUser) {
        // ★ ログイン中のユーザーIDを取得
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        
        // ★ 自分のIDを渡して除外してもらう
        return usersService.searchUsers(query, currentUser.getUsersId());
    }
}