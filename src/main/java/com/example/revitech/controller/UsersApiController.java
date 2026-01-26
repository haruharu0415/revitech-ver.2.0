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

    @GetMapping("/search")
    public List<UserSearchDto> searchUsers(@RequestParam String query) {
        List<Users> searchResults = usersService.searchUsers(query);
        return searchResults.stream()
                .map(user -> new UserSearchDto(user.getUsersId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }
}