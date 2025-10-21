package com.example.revitech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDto {
    // ★ 修正: private Long id; -> private Integer usersId;
    private Integer usersId;
    private String name;
    private String email;
}