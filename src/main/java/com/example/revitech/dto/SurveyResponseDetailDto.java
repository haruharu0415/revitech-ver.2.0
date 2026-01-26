package com.example.revitech.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SurveyResponseDetailDto {
    // ★★★ Serviceがセットしようとしていたフィールド (追加) ★★★
    private Integer reviewId;        // レビューID (これがないとエラーでした)
    private LocalDateTime createdAt; // 作成日時 (これがないとエラーでした)

    // ★★★ 貴方様のコードにあったフィールド (維持) ★★★
    private String studentName;      // 生徒名
    private Integer score;           // 全体満足度
    private String comment;          // 自由コメント
    
    // (補足: Serviceでは createdAt を入れていますが、用途によってはこちらを使ってください)
    private LocalDateTime answeredAt;// 回答日時
    
    // 将来的な拡張用（詳細な質問回答リスト）
    private List<QuestionAnswerDto> details; 

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