package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "teacher_profiles") // タイプミス修正
public class TeacherProfile {

    @Id
    @Column(name = "users_id")
    private Long userId; // users_id が主キー

    @Lob // TEXT型に対応
    private String introduction;

    @Column(name = "icon_picture", nullable = false, length = 255)
    private String iconPicture;

    // // Usersエンティティとの関連付け (任意)
    // @OneToOne
    // @MapsId // 主キーを共有
    // @JoinColumn(name = "users_id")
    // private Users user;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getIconPicture() { return iconPicture; }
    public void setIconPicture(String iconPicture) { this.iconPicture = iconPicture; }
}