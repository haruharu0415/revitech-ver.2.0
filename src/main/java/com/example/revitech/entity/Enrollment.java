package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "enrollments")
// ★ DB定義の UNIQUE(users_id) は意味が異なる。複合UNIQUE(teacher_user_id, student_user_id) が一般的
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB定義に合わせる

    // ★ 本来は teacher_user_id と student_user_id が必要
    @Column(name = "users_id", nullable = false, unique = true) // DB定義はUNIQUEだが通常は違う
    private Long userId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}