package com.example.revitech.dto;

import com.example.revitech.entity.Users; // ★ Users の id は Long
// import java.util.UUID; // ★ UUID は使わない

public class RoomMemberDto {
    private Long id; // ★ 型を Long に戻す
    private String name;
    private String email;

    // ★ Usersエンティティ (getId() は Long を返す) から変換 ★
    public RoomMemberDto(Users user) {
        this.id = user.getId(); // ★ user.getId() は Long を返す
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public RoomMemberDto() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}