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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teacher_reviews")
@Data
@NoArgsConstructor
public class TeacherReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    // ★★★ 修正: レビュー対象の教員ID ★★★
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    // ★★★ 修正: レビューを行った生徒ID ★★★
    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    // 総合評価の点数 (5段階)
    @Column(name = "score")
    private Integer score;

    // 総合コメント
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    // 複数のアンケート回答を関連付けるために、あえてここでは @OneToMany のマッピングは省略します。

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}