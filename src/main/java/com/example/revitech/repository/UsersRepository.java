package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    
    Optional<Users> findByEmail(String email);
    Optional<Users> findByName(String name);
    Optional<Users> findByEmailOrName(String email, String name);

    // ★ 既存のメソッドは維持
    List<Users> findByRole(Integer role);
    
    // ★ 管理画面検索用に追加 (名前またはメールで部分一致検索)
    List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}