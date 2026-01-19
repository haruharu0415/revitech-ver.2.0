package com.example.revitech.dto;

import lombok.Data;

@Data
public class QuestionAverageDto {
    private Integer questionId;
    private String questionText;
    private Double averageScore;

    public QuestionAverageDto(Integer questionId, String questionText, Double averageScore) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.averageScore = averageScore;
    }
}