package com.example.revitech.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern; // メールアドレス判定用にインポート

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.revitech.entity.Users;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersService usersService;

    // 簡単なメールアドレス形式の正規表現 (より厳密なものが必要な場合あり)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        Optional<Users> userOpt;

        // ★★★ 入力がメールアドレス形式か判定 ★★★
        if (isEmail(usernameOrEmail)) {
            // メールアドレスで検索
            userOpt = usersService.findByEmail(usernameOrEmail);
            if (userOpt.isEmpty()) {
                 // メールで見つからなくても、それがユーザー名かもしれないのでここでは例外を投げない
                 // ユーザー名でも検索してみる
                 userOpt = usersService.findByName(usernameOrEmail); // ★ UsersService に findByName が必要
            }
        } else {
            // ユーザー名 (name) で検索
             userOpt = usersService.findByName(usernameOrEmail); // ★ UsersService に findByName が必要
        }
        // ★★★ ここまで ★★★

        // どちらでも見つからなかった場合
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + usernameOrEmail);
        }

        Users user = userOpt.get();

        // 権限リスト作成
        List<GrantedAuthority> authorities = new ArrayList<>();
        Integer roleFlag = user.getRole();
        String roleName;
        if (roleFlag != null) {
            switch (roleFlag) {
                case 1: roleName = "ROLE_ADMIN"; break;
                case 2: roleName = "ROLE_TEACHER"; break;
                case 3: roleName = "ROLE_STUDENT"; break;
                default: roleName = "ROLE_USER";
            }
        } else {
            roleName = "ROLE_USER";
        }
        authorities.add(new SimpleGrantedAuthority(roleName));

        // Spring Security の User オブジェクトを作成
        return new User(
            user.getEmail(), // ★ UserDetails の username としては email を使うのが一般的
            user.getPassword(),
            "active".equals(user.getStatus()),
            true, true, true,
            authorities
        );
    }

    // 簡単なメールアドレス形式チェックメソッド
    private boolean isEmail(String input) {
        if (input == null) {
            return false;
        }
        // JECドメインチェックも追加
        return EMAIL_PATTERN.matcher(input).matches() && input.endsWith("@jec.ac.jp");
    }
}