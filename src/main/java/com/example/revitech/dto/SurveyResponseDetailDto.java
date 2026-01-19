package com.example.revitech.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SurveyResponseDetailDto {
    private String studentName;      // 生徒名
    private Integer score;           // 全体満足度
    private String comment;          // 自由コメント
    private LocalDateTime answeredAt;// 回答日時
    private List<QuestionAnswerDto> details; // 各質問への回答リスト

    @Data
    public static class QuestionAnswerDto {
        private String questionText; // 質問内容
        private Integer score;       // その質問への点数
        
        public QuestionAnswerDto(String questionText, Integer score) {
            this.questionText = questionText;
            this.score = score;
        }
    }
}