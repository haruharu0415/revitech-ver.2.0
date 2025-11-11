package com.example.revitech.entity;

import java.time.LocalDateTime;
// import java.util.UUID; // ★ UUID は使わない

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users") // DBのテーブル名 "Users"
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ★ IDENTITY (自動採番)
    @Column(name = "users_id", updatable = false, nullable = false)
    private Long id; // ★ 型を Long に

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 255) // ★ DBの password VARCHAR(50) より大きいが、BCryptのため 60 以上を推奨 (255が無難)
    private String password;

    @Column(nullable = false, length = 10)
    private String status = "active"; // ★ デフォルト値は @ColumnDefault のがよいが、Java側でも設定

    @Column(nullable = false)
    private Integer role; // 役割 (1: Admin, 2: Teacher, 3: Student など)

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- コンストラクタ ---
    public Users() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}