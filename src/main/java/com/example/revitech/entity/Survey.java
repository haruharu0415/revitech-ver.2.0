package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "surveys")
@Data
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Integer surveyId;

    @Column(name = "title", nullable = false)
    private String title; 

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId; // 作成した先生 (Creator)

    // ★★★ 追加: 結果を紐づける先生のID ★★★
    @Column(name = "target_teacher_id", nullable = false)
    private Integer targetTeacherId; // 結果を紐づける先生 (Review Target)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}