package com.example.revitech.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 10)
    private String status = "active";

    @Column(nullable = false)
    private Integer role; // DBに合わせて Integer

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updatedAt;

    public Users() {}

    // --- ここに Getter / Setter を書く ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ↓↓↓ エラーが出ていた Getter ↓↓↓
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // ↓↓↓ エラーが出ていた Getter ↓↓↓
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ↓↓↓ エラーが出ていた Getter (戻り値は Integer!) ↓↓↓
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; } // Setter も Integer

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // --- Getter / Setter ここまで ---
}