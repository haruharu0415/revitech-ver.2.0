package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient; // 追加

@Entity
@Table(name = "teacher_reviews")
public class TeacherReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_disclosure_requested")
    private Boolean isDisclosureRequested;

    @Column(name = "is_disclosure_granted")
    private Boolean isDisclosureGranted;

    @Column(name = "is_hidden")
    private Integer isHidden = 0;

    @Column(name = "survey_id")
    private Integer surveyId;

    // ★★★ 修正: @Column を削除し @Transient に変更 ★★★
    // これによりDBの列を探さなくなり、エラーが解消します
    @Transient
    private Boolean isChecked = false;

    @PrePersist
    public void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isHidden == null) {
            this.isHidden = 0;
        }
        if (this.isChecked == null) {
            this.isChecked = false;
        }
    }

    // --- Getters and Setters ---
    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsDisclosureRequested() { return isDisclosureRequested; }
    public void setIsDisclosureRequested(Boolean isDisclosureRequested) { this.isDisclosureRequested = isDisclosureRequested; }

    public Boolean getIsDisclosureGranted() { return isDisclosureGranted; }
    public void setIsDisclosureGranted(Boolean isDisclosureGranted) { this.isDisclosureGranted = isDisclosureGranted; }

    public Integer getIsHidden() { return isHidden; }
    public void setIsHidden(Integer isHidden) { this.isHidden = isHidden; }

    public Integer getSurveyId() { return surveyId; }
    public void setSurveyId(Integer surveyId) { this.surveyId = surveyId; }

    public Boolean getIsChecked() { return isChecked; }
    public void setIsChecked(Boolean isChecked) { this.isChecked = isChecked; }

    public Integer getDisclosureStatus() {
        if (Boolean.TRUE.equals(this.isDisclosureGranted)) return 2;
        if (Boolean.TRUE.equals(this.isDisclosureRequested)) return 1;
        return 0;
    }

    public void setDisclosureStatus(Integer status) {
        if (status == null) status = 0;
        if (status == 2) {
            this.isDisclosureGranted = true;
        } else if (status == 1) {
            this.isDisclosureRequested = true;
            this.isDisclosureGranted = false;
        } else {
            this.isDisclosureRequested = false;
            this.isDisclosureGranted = false;
        }
    }
}