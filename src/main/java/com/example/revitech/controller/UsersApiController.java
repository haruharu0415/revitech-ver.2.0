package com.example.revitech.controller;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 名前またはメールアドレスでユーザーを検索するAPI
     * @param query 検索キーワード
     * @return 該当するユーザーのリスト（UserSearchDto形式）
     */
    @GetMapping("/search")
    public List<UserSearchDto> searchUsers(@RequestParam String query) {
        // 【修正】Service層の効率的な検索メソッドを利用
        List<Users> searchResults = usersService.searchUsers(query);

        // 取得したUsersエンティティをDTOに変換して返す
        return searchResults.stream()
                .map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }
}