package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @Column(name = "users_id")
    private Long userId;

    @Lob
    private String introduction;

    @Column(name = "icon_picture", nullable = false, length = 255)
    private String iconPicture;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getIconPicture() { return iconPicture; }
    public void setIconPicture(String iconPicture) { this.iconPicture = iconPicture; }
}