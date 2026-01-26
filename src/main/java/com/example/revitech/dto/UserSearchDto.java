package com.example.revitech.dto;

public class UserSearchDto {
    private Integer usersId;
    private String name;
    private String email;

    public UserSearchDto() {}

    public UserSearchDto(Integer usersId, String name, String email) {
        this.usersId = usersId;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Integer getUsersId() { return usersId; }
    public void setUsersId(Integer usersId) { this.usersId = usersId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}