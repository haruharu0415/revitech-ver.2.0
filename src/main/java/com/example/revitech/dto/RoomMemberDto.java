package com.example.revitech.dto;

import com.example.revitech.entity.Users;

// ルームメンバーの情報をAPIで返すためのDTO
public class RoomMemberDto {
    private Long id;
    private String name;
    private String email;
    
    // Usersエンティティから変換するためのコンストラクタ
    public RoomMemberDto(Users user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    // デフォルトコンストラクタ (Jacksonで必須)
    public RoomMemberDto() {}

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    
    // Setters (省略可だが、Jacksonやフレームワークによっては必要)
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}