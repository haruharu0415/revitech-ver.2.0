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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // ★修正: メールアドレスのみで検索するように変更
        // (findByEmailOrName だと名前重複時にエラーになるため)
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 退会済みチェック
        if ("deleted".equals(user.getStatus())) {
            throw new UsernameNotFoundException("This account has been deleted.");
        }
        
        // 承認待ち(pending)チェック
        if ("pending".equals(user.getStatus())) {
            throw new UsernameNotFoundException("アカウントは承認待ちです。管理者の承認をお待ちください。");
        }

        // 権限リストの作成
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // UserDetailsを返す
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), 
                user.getPassword(),
                authorities
        );
    }
}