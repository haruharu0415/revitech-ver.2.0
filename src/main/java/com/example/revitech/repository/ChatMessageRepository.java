package com.example.revitech.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    // --- 以下の2つのメソッドを追加 ---
    /** 指定時刻以降に作成された、ルーム内のメッセージ数をカウントする */
    long countByRoomIdAndCreatedAtAfter(Long roomId, LocalDateTime timestamp);

    /** ルーム内の最新メッセージを1件取得する */
    Optional<ChatMessage> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);
    // --- 追加メソッドここまで ---
}