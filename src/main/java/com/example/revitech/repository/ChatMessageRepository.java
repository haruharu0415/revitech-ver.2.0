package com.example.revitech.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    // 指定した部屋のメッセージを古い順に取得
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Integer roomId);

    // 指定した部屋の最新メッセージを取得（一覧表示用）
    Optional<ChatMessage> findFirstByRoomIdOrderByCreatedAtDesc(Integer roomId);

    // 未読数をカウント（指定日時より新しいメッセージの数）
    // ※これは互換性のために残していますが、基本的には下のUserIdNotの方を使います
    long countByRoomIdAndCreatedAtAfter(Integer roomId, LocalDateTime timestamp);
    
    // ★★★ 追加: 自分(userId)以外のメッセージで、指定日時より後のものを数える ★★★
    long countByRoomIdAndCreatedAtAfterAndUserIdNot(Integer roomId, LocalDateTime timestamp, Integer userId);

    // 指定した部屋のメッセージを全て削除
    void deleteByRoomId(Integer roomId);
}