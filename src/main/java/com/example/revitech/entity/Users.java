package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Users")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Integer usersId;

    @Column(name = "name", length = 50, nullable = false, unique = true) // ★ unique制約を追加（名前でのログインに備える）
    private String name;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    // ★★★ 最重要修正 ★★★
    // パスワードの長さを50から255に変更します。
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Column(name = "role", nullable = false)
    private Integer role;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}