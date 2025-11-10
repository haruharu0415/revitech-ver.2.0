package com.example.revitech.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberId implements Serializable {

    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "users_id")
    private Integer userId;
}