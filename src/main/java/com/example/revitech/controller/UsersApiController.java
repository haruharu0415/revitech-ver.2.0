package com.example.revitech.controller; 

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/users") 
public class UsersApiController {

    @Autowired
    private UsersService usersService;

    // ユーザーを検索して UserSearchDto のリストを返すAPI
    @GetMapping("/search")
    public List<UserSearchDto> searchUsers(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {

        if (keyword.isBlank()) {
            // キーワードが空の場合は空リストを返す
            return List.of();
        } else {
            return usersService.findUsersByNameOrEmail(keyword);
        }
    }
}