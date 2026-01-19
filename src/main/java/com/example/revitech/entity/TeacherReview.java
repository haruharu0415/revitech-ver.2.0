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
    @Column(name = "review_id") // 明示的に指定
    private Integer reviewId;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(nullable = false)
    private String comment;

    private Integer score;   // 総合点などを入れる場合
    private Integer surveyId; // アンケート回答の場合

    // --- 開示請求機能用のフラグ ---
    @Column(name = "is_disclosure_requested")
    private Boolean isDisclosureRequested = false; // 請求中か

    @Column(name = "is_disclosure_granted")
    private Boolean isDisclosureGranted = false;   // 許可されたか

    // --- 管理用フラグ ---
    @Column(name = "is_hidden")
    private Integer isHidden = 0; // 0:表示, 1:非表示

    @Column(name = "teacher_checked")
    private Integer teacherChecked = 0; // 0:未確認, 1:確認済み

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isDisclosureRequested == null) this.isDisclosureRequested = false;
        if (this.isDisclosureGranted == null) this.isDisclosureGranted = false;
    }

    // --- 便利メソッド: フラグの状態からステータス番号(0,1,2)を返す ---
    public Integer getDisclosureStatus() {
        if (Boolean.TRUE.equals(this.isDisclosureGranted)) {
            return 2; // 許可済み
        } else if (Boolean.TRUE.equals(this.isDisclosureRequested)) {
            return 1; // 請求中
        } else {
            return 0; // 何もなし
        }
    }

    // --- 便利メソッド: ステータス番号(0)をセットするためのダミーメソッド ---
    // Controllerで new TeacherReview() した直後に setDisclosureStatus(0) している箇所への対応
    public void setDisclosureStatus(Integer status) {
        if (status == 0) {
            this.isDisclosureRequested = false;
            this.isDisclosureGranted = false;
        }
        // 1や2の場合は Service 側で個別のフラグを操作するので、ここでは何もしなくてOK
    }
}