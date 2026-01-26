package com.example.revitech.dto;

import java.util.List;

import lombok.Data;

@Data
public class TeacherListDto {
    private Integer usersId;
    private String name;
    private String email;
    private List<String> subjects;
    private Double averageScore;
    private String iconBase64;
    
    // ★ 追加: ハッシュタグリスト
    private List<String> hashtags;

    // コンストラクタ修正
    public TeacherListDto(Integer usersId, String name, String email, List<String> subjects, Double averageScore, String iconBase64, List<String> hashtags) {
        this.usersId = usersId;
        this.name = name;
        this.email = email;
        this.subjects = subjects;
        this.averageScore = averageScore;
        this.iconBase64 = iconBase64;
        this.hashtags = hashtags;
    }
}