package com.example.revitech.dto;

import com.example.revitech.entity.Users;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomMemberDto {

    // ★ 修正: private Long id; -> private Integer usersId;
    private Integer usersId;
    private String name;
    private String email;

    public RoomMemberDto(Users user) {
        // ★ 修正: this.id = user.getId(); -> this.usersId = user.getUsersId();
        this.usersId = user.getUsersId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}