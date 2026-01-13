package com.example.revitech.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // ★★★ 修正: メールアドレス または 名前(ユーザー名) で検索 ★★★
        // 入力された値(input)を、emailとnameの両方の条件で探します
        Users user = usersRepository.findByEmailOrName(input, input)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or name: " + input));

        // 退会済みチェック
        if ("deleted".equals(user.getStatus())) {
            throw new UsernameNotFoundException("This account has been deleted.");
        }

        // 権限リストの作成
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // UserDetailsを返す
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // 認証後の識別子にはユニークなEmailを使用するのが安全
                user.getPassword(),
                authorities
        );
    }
}