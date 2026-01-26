package com.example.revitech.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    // 指定した部屋のメッセージを古い順に取得
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Integer roomId);

    // 指定した部屋の最新メッセージを取得（一覧表示用）
    java.util.Optional<ChatMessage> findFirstByRoomIdOrderByCreatedAtDesc(Integer roomId);

    // 未読数をカウント（指定日時より新しいメッセージの数）
    long countByRoomIdAndCreatedAtAfter(Integer roomId, LocalDateTime timestamp);

    // ★★★ 追加: 指定した部屋のメッセージを全て削除 ★★★
    void deleteByRoomId(Integer roomId);
}