package com.example.revitech.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "review_question")
public class ReviewQuestion {

    @EmbeddedId
    private ReviewQuestionId id;

    // // 関連エンティティへのマッピング (任意)
    // @ManyToOne
    // @MapsId("reviewId")
    // @JoinColumn(name = "review_id")
    // private TeacherReview review;

    // @ManyToOne
    // @MapsId("questionId")
    // @JoinColumn(name = "question_id")
    // private Question question;

    // ★ レビュー評価の値を保存する列が必要 (例: rating INT)

    public ReviewQuestion() {}

    public ReviewQuestion(ReviewQuestionId id) {
        this.id = id;
    }

    public ReviewQuestion(Long reviewId, Long questionId) {
        this.id = new ReviewQuestionId(reviewId, questionId);
    }

    // Getters and Setters
    public ReviewQuestionId getId() { return id; }
    public void setId(ReviewQuestionId id) { this.id = id; }
}