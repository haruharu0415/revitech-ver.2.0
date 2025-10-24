package com.example.revitech.controller; // パッケージ名は実際の場所に合わせてください

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/users") // APIのベースパス (例)
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
            // ★ UsersService の検索メソッドを呼び出す
            return usersService.findUsersByNameOrEmail(keyword);
        }
    }

    // 他のユーザー関連API (例: IDでユーザー取得など) があればここに追加...
    // @GetMapping("/{userId}")
    // public UserSearchDto getUserById(@PathVariable Long userId) { ... }
}