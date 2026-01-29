package com.example.revitech.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyResponseDetailDto {

    private Integer reviewId;
    private String studentName;
    private LocalDateTime answeredAt;
    private Integer score;
    private String comment;
    private List<Detail> details;

    public SurveyResponseDetailDto() {
    }

    public SurveyResponseDetailDto(Integer reviewId, String studentName, LocalDateTime answeredAt, Integer score, String comment, List<Detail> details) {
        this.reviewId = reviewId;
        this.studentName = studentName;
        this.answeredAt = answeredAt;
        this.score = score;
        this.comment = comment;
        this.details = details;
    }

    // ★★★ エラー解消用: この内部クラス(Detail)が必要です ★★★
    public static class Detail {
        private String questionText;
        private Integer score;

        public Detail(String questionText, Integer score) {
            this.questionText = questionText;
            this.score = score;
        }

        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }
    }

    // Getters and Setters
    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public List<Detail> getDetails() { return details; }
    public void setDetails(List<Detail> details) { this.details = details; }
}