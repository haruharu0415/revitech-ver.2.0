package com.example.revitech.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id") // DB列名 room_id
    private Long id; // Java変数名

    @Column(length = 50)
    private String name;

    @Column(nullable = false)
    private Integer type; // DBデータ型 INT

    @Column(name = "users_id", nullable = false) // DB列名 users_id
    private Long creatarUserId; // Java変数名 (以前の経緯からこのまま)

    // ★ フィールド名を createdAt (キャメルケース) に変更 ★
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false) // DB列名は created_at
    private LocalDateTime createdAt; // ← ここを変更

    public ChatRoom() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Long getCreatarUserId() { return creatarUserId; }
    public void setCreatarUserId(Long creatarUserId) { this.creatarUserId = creatarUserId; }

    // ★ Getter/Setter 名も createdAt に変更 ★
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    // --- End of Getters and Setters ---
}