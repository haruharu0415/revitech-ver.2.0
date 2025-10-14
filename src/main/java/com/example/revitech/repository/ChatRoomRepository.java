package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.revitech.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 【最終確認クエリ】二重引用符を外し、最も一般的なスネークケースのネイティブSQLに戻す
     * DBの列名（room_id, user_id, type）に完全に合わせる
     */
    @Query(value = "SELECT r.* FROM chat_rooms r " +
                   "JOIN chat_members m1 ON r.id = m1.room_id " +
                   "JOIN chat_members m2 ON r.id = m2.room_id " +
                   "WHERE r.type = 'DM' AND m1.user_id = :userId1 AND m2.user_id = :userId2", 
           nativeQuery = true)
    Optional<ChatRoom> findExistingDmRoom(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}