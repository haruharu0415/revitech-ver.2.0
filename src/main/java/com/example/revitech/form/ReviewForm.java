package com.example.revitech.form;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ReviewForm {
    private Integer score;
    private String comment;
    private Integer teacherId;
    
    // ★★★ 追加 ★★★
    private Integer surveyId;

    private Map<Integer, Integer> answers = new HashMap<>();
}