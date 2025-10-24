package com.example.revitech.entity;

// import java.util.UUID; // ★ UUID は使わない
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "teacher_profiles")
public class TeacherProfile {

    @Id
    @Column(name = "users_id")
    private Long userId; // ★ 型を Long に

    @Column(columnDefinition = "TEXT") // DBの TEXT 型に対応
    private String introduction;

    @Column(name = "icon_picture", length = 255, nullable = false)
    private String iconPicture;

    // Usersエンティティへの関連付け (Users.id も Long)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // userId フィールドが主キーであり、Usersへの外部キーでもあることを示す
    @JoinColumn(name = "users_id")
    private Users user;

    // --- コンストラクタ ---
    public TeacherProfile() {}

    // --- Getters and Setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }

    public String getIconPicture() { return iconPicture; }
    public void setIconPicture(String iconPicture) { this.iconPicture = iconPicture; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
}