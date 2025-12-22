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
@Table(name = "teacher_reviews")
@Data
public class TeacherReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "survey_id")
    private Integer surveyId;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "comment", columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    @Column(name = "is_hidden", nullable = false)
    private Integer isHidden = 0;

    @Column(name = "disclosure_status", nullable = false)
    private Integer disclosureStatus = 0;

    // ★★★ 追加: 先生の確認フラグ (0:未確認, 1:確認済み) ★★★
    @Column(name = "teacher_checked", nullable = false)
    private Integer teacherChecked = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}