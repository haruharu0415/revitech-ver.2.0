package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatReadStatus;

public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {

    // 特定のユーザーの、特定のルームでの既読情報を検索する
    Optional<ChatReadStatus> findByUserIdAndRoomId(Long userId, Long roomId);
}