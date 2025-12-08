package com.example.revitech.form;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ReviewForm {

    // 総合評価 (1-5)
    private Integer score;

    // 総合コメント
    private String comment;

    // 教員ID (hiddenで送信)
    private Integer teacherId;

    // 個別の質問への回答
    // Key: 質問ID (questionId), Value: 点数 (score)
    private Map<Integer, Integer> answers = new HashMap<>();
}