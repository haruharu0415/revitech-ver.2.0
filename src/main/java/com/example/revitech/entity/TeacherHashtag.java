package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teacher_hashtags")
@Data
public class TeacherHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id") // DBのカラム名に合わせてください（例: id, hashtag_id）
    private Integer hashtagId;

    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "hashtag")
    private String hashtag;
}