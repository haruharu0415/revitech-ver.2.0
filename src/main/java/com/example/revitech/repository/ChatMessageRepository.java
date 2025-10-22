package com.example.revitech.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // ★ メソッド名を CreatedAt (キャメルケース) に変更 ★
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    // ★ メソッド名を CreatedAt (キャメルケース) に変更 ★
    long countByRoomIdAndCreatedAtAfter(Long roomId, LocalDateTime timestamp);

    // ★ メソッド名を CreatedAt (キャメルケース) に変更 ★
    Optional<ChatMessage> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);
}