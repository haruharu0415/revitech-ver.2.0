package com.example.revitech;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@SpringBootApplication
public class RevitechApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevitechApplication.class, args);
    }

    /**
     * ★★★ このBeanをファイルの一番下に追加してください ★★★
     * アプリケーション起動時にサンプルデータ（教員アカウント）をDBに自動で作成します。
     * これにより、教員は「本物の」ユーザーとなり、全ての機能が正常に動作します。
     */
    @Bean
    CommandLineRunner run(UsersService usersService, PasswordEncoder passwordEncoder) {
        return args -> {
            // --- 福井先生 ---
            if (usersService.findByName("福井先生").isEmpty()) {
                Users fukui = new Users();
                fukui.setName("福井先生");
                fukui.setEmail("fukui@example.com");
                fukui.setPassword(passwordEncoder.encode("password")); // パスワードは "password" で統一
                fukui.setRole(2); // 2: 教員
                fukui.setStatus("active");
                usersService.saveRawUser(fukui);
            }
            // --- 佐藤先生 ---
            if (usersService.findByName("佐藤先生").isEmpty()) {
                Users sato = new Users();
                sato.setName("佐藤先生");
                sato.setEmail("sato@example.com");
                sato.setPassword(passwordEncoder.encode("password"));
                sato.setRole(2);
                sato.setStatus("active");
                usersService.saveRawUser(sato);
            }
            // --- 柴田先生 ---
            if (usersService.findByName("柴田先生").isEmpty()) {
                Users shibata = new Users();
                shibata.setName("柴田先生");
                shibata.setEmail("shibata@example.com");
                shibata.setPassword(passwordEncoder.encode("password"));
                shibata.setRole(2);
                shibata.setStatus("active");
                usersService.saveRawUser(shibata);
            }
            // --- 河野先生 ---
            if (usersService.findByName("河野先生").isEmpty()) {
                Users kono = new Users();
                kono.setName("河野先生");
                kono.setEmail("kono@example.com");
                kono.setPassword(passwordEncoder.encode("password"));
                kono.setRole(2);
                kono.setStatus("active");
                usersService.saveRawUser(kono);
            }
            // --- 小宮山先生 ---
            if (usersService.findByName("小宮山先生").isEmpty()) {
                Users komiyama = new Users();
                komiyama.setName("小宮山先生");
                komiyama.setEmail("komiyama@example.com");
                komiyama.setPassword(passwordEncoder.encode("password"));
                komiyama.setRole(2);
                komiyama.setStatus("active");
                usersService.saveRawUser(komiyama);
            }
        };
    }
}