package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    
    Optional<Users> findByEmail(String email);
    
    Optional<Users> findByName(String name);
    
    // ★★★ 追加: メールアドレス または 名前 で検索するメソッド ★★★
    Optional<Users> findByEmailOrName(String email, String name);

    // (既存のメソッドがあればそのまま残してください)
    List<Users> findByRole(Integer role);
    List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}