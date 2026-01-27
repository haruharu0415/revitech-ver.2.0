package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "review_answers") // ★修正: 実在するテーブル名
@Data
public class SurveyAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Integer answerId;

    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    @Column(name = "score")
    private Integer score;
}