package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teacher_reviews")
@Data
public class TeacherReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    // ★★★ どの教員へのレビューかを示すID (重要) ★★★
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "users_id", nullable = false)
    private Integer usersId;

    // ★★★ 5段階評価の点数を追加 ★★★
    @Column(name = "score")
    private Integer score;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // (PrePersist, PreUpdateは省略)
}