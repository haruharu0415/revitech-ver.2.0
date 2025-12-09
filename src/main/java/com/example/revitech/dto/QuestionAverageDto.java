package com.example.revitech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAverageDto {
    private Integer questionId;
    private String questionBody;
    private int totalAnswers;
    private double averageScore;
}