package com.example.revitech.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeacherListDto {

    private Integer usersId;
    private String name;
    private String email;
    private List<String> subjects;
    private Double averageReviewScore;

    public TeacherListDto(Integer usersId, String name, String email, List<String> subjects, Double averageReviewScore) {
        this.usersId = usersId;
        this.name = name;
        this.email = email;
        this.subjects = subjects;
        this.averageReviewScore = averageReviewScore != null ? averageReviewScore : 0.0; // NULLの場合は0.0に
    }

    // レビューの星評価（整数部）を取得するヘルパーメソッド
    public int getStarsFull() {
        return averageReviewScore.intValue();
    }

    // レビューの星評価（小数部が0.5以上か）を取得するヘルパーメソッド
    public boolean isHasHalfStar() {
        return (averageReviewScore - getStarsFull()) >= 0.5;
    }

    // レビューの空の星の数を取得するヘルパーメソッド
    public int getStarsEmpty() {
        int full = getStarsFull();
        boolean half = isHasHalfStar();
        return 5 - full - (half ? 1 : 0);
    }
}