package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    // メールアドレスでの完全一致検索（ログイン時などに使用）
    Optional<Users> findByEmail(String email);

    // 名前での完全一致検索
    Optional<Users> findByName(String name);

    // 役割（Role）による検索（教員一覧などで使用）
    List<Users> findByRole(Integer role);

    /**
     * ★★★ 曖昧検索用メソッド ★★★
     * 名前 または メールアドレス に指定された文字列が含まれているユーザーを検索します。
     * IgnoreCase により、大文字・小文字を区別せずに検索します。
     */
    List<Users> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}