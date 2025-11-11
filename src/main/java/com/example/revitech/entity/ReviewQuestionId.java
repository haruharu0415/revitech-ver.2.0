package com.example.revitech.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReviewQuestionId implements Serializable {

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "question_id")
    private Long questionId;

     public ReviewQuestionId() {}

    public ReviewQuestionId(Long reviewId, Long questionId) {
        this.reviewId = reviewId;
        this.questionId = questionId;
    }

    // Getters, Setters, hashCode, equals
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewQuestionId that = (ReviewQuestionId) o;
        return Objects.equals(reviewId, that.reviewId) && Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, questionId);
    }
}