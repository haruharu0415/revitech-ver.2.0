package com.example.revitech.dto;

// import java.util.UUID; // ★ UUID は使わない

public class TeacherListDto {
    private Long id; // ★ 型を Long に戻す
    private String name;
    private String iconUrl;
    private double averageRating;

    // ★ コンストラクタの第一引数の型を Long に戻す ★
    public TeacherListDto(Long id, String name, String iconUrl, double averageRating) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.averageRating = averageRating;
    }

    // Getters
    public Long getId() { return id; } // ★ 戻り値の型を Long に
    public String getName() { return name; }
    public String getIconUrl() { return iconUrl; }
    public double getAverageRating() { return averageRating; }

    // Setters
    public void setId(Long id) { this.id = id; } // ★ 引数の型を Long に
    public void setName(String name) { this.name = name; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}