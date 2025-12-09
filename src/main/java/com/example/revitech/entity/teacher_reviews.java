package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist; // 追記
import jakarta.persistence.PreUpdate; // 追記
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teacher_reviews")
@Data
public class teacher_reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    // ★ 修正点: Teacherエンティティへの参照を削除し、IDを直接保持
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    // ★ 修正点: Userエンティティへの参照を削除し、IDを直接保持
    @Column(name = "users_id", nullable = false)
    private Integer usersId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // エンティティ保存前（INSERT時）に実行される処理
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // エンティティ更新前（UPDATE時）に実行される処理
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}