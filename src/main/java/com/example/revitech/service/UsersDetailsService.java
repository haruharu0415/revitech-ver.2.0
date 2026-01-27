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
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // ★★★ 修正箇所: findByEmail ではなく findByNameOrEmail を使います ★★★
        // これで、入力された文字列が「名前」でも「メール」でも、どちらかにヒットすればログインできます
        Users user = usersRepository.findByNameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        // 承認待ち(pending)や削除済み(deleted)のユーザーはログインさせないチェック
        if ("pending".equals(user.getStatus())) {
            throw new UsernameNotFoundException("このアカウントは承認待ちです。");
        }
        if ("deleted".equals(user.getStatus())) {
            throw new UsernameNotFoundException("このアカウントは削除されています。");
        }

        // 権限の設定 (Role=3ならADMIN, それ以外はUSERなど)
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() == 3) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        // 先生(Role=2)用の権限も必要なら追加
        if (user.getRole() == 2) {
             authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
        }

        // Spring Security用のユーザー情報を返す
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // または user.getName()。認証の識別子として使いたい方
                user.getPassword(),
                authorities
        );
    }
}