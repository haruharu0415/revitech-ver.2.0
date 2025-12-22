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
import jakarta.persistence.Transient; // 追加
import lombok.Data;

@Entity
@Table(name = "Users")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Integer usersId;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Column(name = "role", nullable = false)
    private Integer role;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
    
    // ★★★ エラー回避用に追加：アイコンURL取得用メソッド ★★★
    // データベースには保存しない一時的なフィールドとして定義
    @Transient
    public String getIconUrl() {
        // 本来は TeacherProfile や StudentProfile から取得すべきですが、
        // 今はエラー回避のため null (デフォルト画像を表示させる) を返します。
        return null;
    }
}