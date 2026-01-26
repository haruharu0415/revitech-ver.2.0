package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatReadStatus;

@Repository
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Integer> {

    // ユーザーと部屋IDで既読情報を取得
    Optional<ChatReadStatus> findByUserIdAndRoomId(Integer userId, Integer roomId);

    // ★★★ 追加: 指定した部屋の既読情報を全て削除 ★★★
    void deleteByRoomId(Integer roomId);
}