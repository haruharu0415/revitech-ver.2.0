package com.example.revitech.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.revitech.dto.UserSearchDto; // ★新規作成を推奨
import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@RestController
@RequestMapping("/api/users")
public class UsersApiController {

    private final UsersService usersService;

    public UsersApiController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 名前またはメールアドレスでユーザーを検索
    @GetMapping("/search")
    public List<UserSearchDto> searchUsers(@RequestParam String query) {
        // TODO: UsersRepository に findByNameContainingOrEmailContaining(query, query) 
        // のようなメソッドを追加し、検索を行うのが最適です。
        
        // 暫定: 全ユーザーを取得してからフィルター
        List<Users> allUsers = usersService.findAll();
        
        // 大文字小文字を区別しない検索
        String lowerQuery = query.toLowerCase();

        return allUsers.stream()
                .filter(user -> 
                    user.getName().toLowerCase().contains(lowerQuery) || 
                    user.getEmail().toLowerCase().contains(lowerQuery)
                )
                .map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }
}
