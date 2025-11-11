package com.example.revitech.repository;

import java.util.List; // ★ インポート追加
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Users;

// ★ 主キーの型は Long
public interface UsersRepository extends JpaRepository<Users, Long> {

    // (ログイン用)
    Optional<Users> findByEmail(String email);
    
    // (名前重複チェック / ログイン用)
    Optional<Users> findByName(String name);

    // ★★★ 以下2つを追加 ★★★
    
    // (メール重複チェック (isEmailTaken) 用)
    boolean existsByEmail(String email);
    
    // (ユーザー検索 (findUsersByNameOrEmail) 用)
    List<Users> findByNameContainingOrEmailContaining(String name, String email);
}