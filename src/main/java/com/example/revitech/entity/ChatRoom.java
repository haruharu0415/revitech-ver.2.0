package com.example.revitech.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
// ★★★ 修正点: @JsonIgnoreProperties をインポート ★★★
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_rooms")
// ★★★ 修正点: この1行を追加 ★★★
// (Lazy Loading のプロキシ情報をJSON変換時に無視させる)
@JsonIgnoreProperties({"hibernateLazyInitializer"}) 
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id; 

    @Column(length = 50)
    private String name;

    @Column(nullable = false)
    private Integer type; // 1: DM, 2: Group

    @Column(name = "users_id", nullable = false)
    private Long creatorUserId; 

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // (前回追加した @JsonIgnore はそのまま)
    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatMember> members = new HashSet<>();

    // --- コンストラクタ ---
    public ChatRoom() {}

    public ChatRoom(Integer type, Long creatorUserId) {
        this.type = type;
        this.creatorUserId = creatorUserId;
        this.name = "DM";
    }

    public ChatRoom(String name, Integer type, Long creatorUserId) {
        this.name = name;
        this.type = type;
        this.creatorUserId = creatorUserId;
    }

    // --- Getters and Setters (省略なし) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Long getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(Long creatorUserId) { this.creatorUserId = creatorUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<ChatMember> getMembers() { return members; }
    public void setMembers(Set<ChatMember> members) { this.members = members; }

    // --- ヘルパーメソッド ---
    public void addMember(ChatMember member) {
        members.add(member);
        member.setRoom(this);
    }

    public void removeMember(ChatMember member) {
        members.remove(member);
        member.setRoom(null);
    }
}