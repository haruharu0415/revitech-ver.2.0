package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> { // 必要なら JpaSpecificationExecutor<Users> も継承

    Optional<Users> findByEmail(String email);

    // ★★★ このメソッド定義を追加 ★★★
    Optional<Users> findByName(String name);
    // ★★★ ここまで ★★★

    // List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String nameKeyword, String emailKeyword); // 検索用 (必要なら)
}