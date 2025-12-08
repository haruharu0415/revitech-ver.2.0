package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_answers")
@Data
@NoArgsConstructor
public class ReviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer answerId;

    // ★★★ 外部キー: どのレビューに属するか (TeacherReview.review_id) ★★★
    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    // ★★★ 外部キー: どの質問に対する回答か (Question.question_id) ★★★
    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    // ★★★ 回答の点数 (例: 5段階評価) ★★★
    @Column(name = "score", nullable = false)
    private Integer score;
}