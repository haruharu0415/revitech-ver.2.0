package com.example.revitech.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "name")
    private String name;

    // ★★★ 修正: DBに列が存在するため、@Column に戻しました ★★★
    @Column(name = "users_id")
    private Integer usersId;

    // isDm は計算項目なので @Transient のままでOK
    @Transient
    private Integer isDm;

    // DBにある type カラム
    @Column(name = "type")
    private Integer type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        // typeが未設定ならデフォルトでグループ(2)にする
        if (this.type == null) {
            this.type = 2;
        }
    }

    // --- Getters and Setters ---

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUsersId() {
        return usersId;
    }

    public void setUsersId(Integer usersId) {
        this.usersId = usersId;
    }

    // isDm の Getter
    public Integer getIsDm() {
        if (this.type != null && this.type == 1) {
            return 1; // DM
        }
        return 0; // グループ
    }

    // isDm の Setter
    public void setIsDm(Integer isDm) {
        this.isDm = isDm;
        if (isDm != null && isDm == 1) {
            this.type = 1; // DM
        } else {
            this.type = 2; // グループ
        }
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
        // typeがセットされたら isDm も更新しておく
        if (type != null && type == 1) {
            this.isDm = 1;
        } else {
            this.isDm = 0;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}