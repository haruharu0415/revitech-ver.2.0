package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    // ★★★ 最新のメッセージを1件取得（未読判定で使用） ★★★
    Optional<ChatMessage> findTopByRoomIdOrderByCreatedAtDesc(Integer roomId);

    // チャット履歴用（古い順）
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Integer roomId);
}