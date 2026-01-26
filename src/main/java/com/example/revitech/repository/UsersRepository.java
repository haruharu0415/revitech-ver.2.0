package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    
    // メールアドレスで完全一致検索
    Optional<Users> findByEmail(String email);
    
    // 名前で完全一致検索
    Optional<Users> findByName(String name);
    
    // ★★★ 重要: ログインや重複チェックで使用 (名前 OR メール) ★★★
    Optional<Users> findByNameOrEmail(String name, String email);
    
    // 権限(Role)で検索 (教員リストなどで使用)
    List<Users> findByRole(Integer role);
    
    // ★★★ 重要: 検索機能 (SearchController) で使用 ★★★
    // 名前またはメールアドレスにキーワードを含むものを検索 (大文字小文字を区別しない)
    List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}