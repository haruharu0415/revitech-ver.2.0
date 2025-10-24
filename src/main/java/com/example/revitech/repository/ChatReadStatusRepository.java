package com.example.revitech.repository;

import java.util.Optional;

// import java.util.UUID; // ★ UUID は使わない
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatReadStatus;

// ★ ChatReadStatus の主キー型 (id) は Long (BIGINT) なので変更なし ★
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {

    // ★ userId と roomId の型を Long に戻す ★
    Optional<ChatReadStatus> findByUserIdAndRoomId(Long userId, Long roomId);
}