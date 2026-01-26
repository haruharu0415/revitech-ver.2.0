package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Integer usersId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private Integer role;

    @Column(name = "status")
    private String status;

    @Column(name = "chat_sort_order")
    private Integer chatSortOrder;

    // --- 作成日時関連 ---

    // 本来の正しいスペル
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // DBに残っている古いスペル ("d"なし)
    @Column(name = "create_at")
    private LocalDateTime createAt;


    // --- 更新日時関連（★今回の修正箇所） ---

    // 本来の正しいスペル
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ★追加: DBに残っている古いスペル ("d"なし)
    @Column(name = "update_at")
    private LocalDateTime updateAt;


    // --- イベントリスナー ---

    // 新規登録時 (INSERT前)
    @PrePersist
    public void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        
        // 作成日時をセット
        this.createdAt = now;
        this.createAt = now; 

        // 更新日時もセット (★ updateAt も忘れずに！)
        this.updatedAt = now;
        this.updateAt = now; 

        // デフォルト値の設定
        if (this.chatSortOrder == null) {
            this.chatSortOrder = 1; 
        }
        if (this.status == null) {
            this.status = "active";
        }
    }

    // 更新時 (UPDATE前)
    @PreUpdate
    public void onPreUpdate() {
        LocalDateTime now = LocalDateTime.now();
        
        // 更新日時を現在時刻で上書き (★ updateAt も更新！)
        this.updatedAt = now;
        this.updateAt = now;
    }

    // --- ゲッター・セッター ---

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getChatSortOrder() {
        return chatSortOrder;
    }

    public void setChatSortOrder(Integer chatSortOrder) {
        this.chatSortOrder = chatSortOrder;
    }

    // --- CreatedAt ---
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    // --- UpdatedAt ---
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ★追加: updateAt用
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}