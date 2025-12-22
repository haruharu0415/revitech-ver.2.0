package com.example.revitech.entity;

import java.time.LocalDateTime;
import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teacher_profiles")
@Data
public class TeacherProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileId;

    @Column(name = "teacher_id", nullable = false, unique = true)
    private Integer teacherId;

    @Column(name = "introduction", columnDefinition = "NVARCHAR(MAX)")
    private String introduction;

    // 画像データ本体 (バイナリ)
    @Lob
    @Column(name = "icon_data")
    private byte[] iconData;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ★HTML表示用: バイナリをBase64文字列に変換するメソッド
    public String getIconBase64() {
        if (iconData == null || iconData.length == 0) return null;
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(iconData);
    }
}