package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
    List<Users> findAll();
    Optional<Users> findByEmail(String email);
    

    /**
     * 名前またはメールアドレスで部分一致検索を行う
     * @param nameKeyword 検索キーワード（名前用）
     * @param emailKeyword 検索キーワード（メール用）
     * @return 該当するユーザーのリスト
     */
    List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String nameKeyword, String emailKeyword);
}