package com.example.revitech.dto;

import lombok.Data;

@Data
public class TeacherScoreDto {
    private Integer questionId;
    private String questionText; // 項目名
    private Double averageScore; // 平均点

    public TeacherScoreDto(Integer questionId, String questionText, Double averageScore) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.averageScore = averageScore;
    }
}