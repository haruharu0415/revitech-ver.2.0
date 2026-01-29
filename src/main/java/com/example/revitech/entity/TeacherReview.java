package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

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

    // ★★★ エラー解消用: このフィールドが必要です ★★★
    @Column(name = "survey_id")
    private Integer surveyId;

    @PrePersist
    public void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isHidden == null) {
            this.isHidden = 0;
        }
    }

    // Getters and Setters
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
}