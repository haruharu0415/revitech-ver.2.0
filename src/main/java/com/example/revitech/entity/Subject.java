package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    // ★ DB定義ではUNIQUEだが、科目名が UNIQUE であるべき
    // ★ DB定義の外部キー (users_id) は意味不明なので削除
    @Column(name = "subject_name", nullable = false, length = 20, unique = true)
    private String name; // Javaでは name の方が自然

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}