package com.example.revitech.repository;

import java.time.LocalDateTime; // ★ インポート追加
import java.util.List;
import java.util.Optional; // ★ インポート追加

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ★ インポート追加
import org.springframework.data.repository.query.Param; // ★ インポート追加

import com.example.revitech.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    // ★★★ 以下2つのメソッドを追加 ★★★

    /**
     * 指定されたルームの最新メッセージの時刻を取得する
     */
    @Query("SELECT MAX(cm.createdAt) FROM ChatMessage cm WHERE cm.roomId = :roomId")
    Optional<LocalDateTime> findLatestMessageTimestampByRoomId(@Param("roomId") Long roomId);

    /**
     * 指定されたルームで、指定された時刻より後に投稿されたメッセージ（＝未読）の件数を取得する
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.createdAt > :lastReadTime")
    int countUnreadMessages(@Param("roomId") Long roomId, @Param("lastReadTime") LocalDateTime lastReadTime);
}