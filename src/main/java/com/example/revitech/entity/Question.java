package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "question")
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    // ★★★ 追加: どのアンケートの質問か ★★★
    @Column(name = "survey_id", nullable = false)
    private Integer surveyId;

    @Column(name = "question_body", nullable = false)
    private String questionBody;
}