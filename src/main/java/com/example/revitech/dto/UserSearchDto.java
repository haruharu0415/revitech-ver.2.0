package com.example.revitech.dto;

public class UserSearchDto {
    private Integer usersId;
    private String name;
    private String email;
    // ★追加フィールド
    private String iconUrl;
    private Integer role;

    public UserSearchDto() {}

    // ★★★ 重要：既存の機能を壊さないために、このコンストラクタは絶対に消さない ★★★
    public UserSearchDto(Integer usersId, String name, String email) {
        this.usersId = usersId;
        this.name = name;
        this.email = email;
    }

    // ★★★ 新規：アイコンとロールを含んだ新しいコンストラクタ ★★★
    public UserSearchDto(Integer usersId, String name, String email, String iconUrl, Integer role) {
        this.usersId = usersId;
        this.name = name;
        this.email = email;
        this.iconUrl = iconUrl;
        this.role = role;
    }

    // Getters and Setters
    public Integer getUsersId() { return usersId; }
    public void setUsersId(Integer usersId) { this.usersId = usersId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ★追加のGetter/Setter
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
}