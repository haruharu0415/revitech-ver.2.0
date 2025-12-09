package com.example.revitech.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student_profiles")
@Data
public class StudentProfile {

    @Id
    @Column(name = "users_id")
    private Integer usersId;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "icon_picture", length = 255, nullable = false)
    private String iconPicture;
}