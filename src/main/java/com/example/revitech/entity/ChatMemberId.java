package com.example.revitech.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "users_id")
    private Integer usersId;
}