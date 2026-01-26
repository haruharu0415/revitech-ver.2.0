package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "review_answers")
@Data
public class ReviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Integer answerId;

    // どの大元の回答(TeacherReview)に紐づくか
    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    // どの質問(Question)に対する答えか
    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    // 点数 (1~5)
    @Column(name = "score", nullable = false)
    private Integer score;

    // どの先生への回答か
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    // ★★★ これらが不足していたので追加！ ★★★
    @Column(name = "survey_id", nullable = true) // アンケート回答でない場合はnullもあり得るためnullable推奨ですが、仕様によります
    private Integer surveyId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;
}