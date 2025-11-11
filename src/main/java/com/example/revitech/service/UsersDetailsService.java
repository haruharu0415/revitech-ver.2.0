package com.example.revitech.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.revitech.entity.Users;
import com.example.revitech.repository.UsersRepository;

@Service
public class UsersDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public UsersDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ★★★ ロジックを全面的に修正 ★★★
        
        Optional<Users> userOptional;
        
        // 入力された'username'に'@'が含まれているかで、メールアドレスか名前かを判断
        if (username.contains("@")) {
            // メールアドレスとして検索
            userOptional = usersRepository.findByEmail(username);
        } else {
            // 名前として検索
            userOptional = usersRepository.findByName(username);
        }

        // ユーザーが見つからなかった場合、例外をスロー
        Users user = userOptional.orElseThrow(() -> 
            new UsernameNotFoundException("ユーザーが見つかりません: " + username)
        );

        String roleStr = convertRoleToString(user.getRole());

        return User.builder()
                .username(user.getEmail()) // Spring Security内部では引き続きemailを 'username' として扱う
                .password(user.getPassword())
                .roles(roleStr)
                .build();
    }

    private String convertRoleToString(Integer role) {
        if (role == null) return "USER";
        return switch (role) {
            case 1 -> "STUDENT";
            case 2 -> "TEACHER";
            case 3 -> "ADMIN";
            default -> "USER";
        };
    }
}