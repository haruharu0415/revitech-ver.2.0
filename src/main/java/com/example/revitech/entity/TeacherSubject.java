package com.example.revitech.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "teacher_subject")
@Data
public class TeacherSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "users_id", nullable = false)
    private Integer usersId;

    @Column(name = "subject_id", nullable = false)
    private Integer subjectId;
}