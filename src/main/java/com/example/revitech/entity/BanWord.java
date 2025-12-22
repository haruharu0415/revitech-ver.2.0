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
@Table(name = "ban_words")
@Data
public class BanWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private Integer banId;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}