package com.example.revitech.dto;

// import java.util.UUID; // ★ UUID は使わない

public class UserProfileDto {
    private Long id; // ★ 型を Long に戻す
    private String name;
    private String iconUrl;

    // ★ コンストラクタの第一引数の型を Long に戻す ★
    public UserProfileDto(Long id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public UserProfileDto() {}

    // Getters
    public Long getId() { return id; } // ★ 戻り値の型を Long に
    public String getName() { return name; }
    public String getIconUrl() { return iconUrl; }

    // Setters
    public void setId(Long id) { this.id = id; } // ★ 引数の型を Long に
    public void setName(String name) { this.name = name; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
}