package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teacher_profiles")
@Data
public class TeacherProfile {

    @Id
    @Column(name = "users_id")
    private Integer usersId;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "icon_picture", length = 255, nullable = false)
    private String iconPicture;
}