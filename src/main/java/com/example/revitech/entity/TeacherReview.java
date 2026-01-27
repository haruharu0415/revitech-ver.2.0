package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ★★★ 以下、追加したフィールド (これが不足していました！) ★★★

    // 0:なし, 1:請求中, 2:開示済み
    @Column(name = "disclosure_status")
    private Integer disclosureStatus;

    // 開示請求がされたか
    @Column(name = "is_disclosure_requested")
    private Boolean isDisclosureRequested;

    // 開示が許可されたか
    @Column(name = "is_disclosure_granted")
    private Boolean isDisclosureGranted;

    // 先生が確認したか (通知用)
    @Column(name = "teacher_checked")
    private Integer teacherChecked;

    // 非表示フラグ (0:表示, 1:非表示)
    @Column(name = "is_hidden")
    private Integer isHidden;
    
    // アンケート機能との連携用
    @Column(name = "survey_id")
    private Integer surveyId;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // デフォルト値の設定
        if (disclosureStatus == null) disclosureStatus = 0;
        if (isDisclosureRequested == null) isDisclosureRequested = false;
        if (isDisclosureGranted == null) isDisclosureGranted = false;
        if (teacherChecked == null) teacherChecked = 0;
        if (isHidden == null) isHidden = 0;
    }
}