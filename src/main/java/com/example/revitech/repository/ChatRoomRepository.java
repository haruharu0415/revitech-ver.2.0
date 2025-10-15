// ChatRoomRepository.java の全文
package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.revitech.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * ▼▼▼【修正点】2ユーザーが参加するDMルームを厳密に1件だけ検索するクエリに変更 ▼▼▼
     * 2人のユーザーIDを含み、かつ、参加メンバーがちょうど2人だけのDMルームを検索することで、
     * 不正なデータがあっても意図したルームを1件だけ取得できるようにします。
     */
    @Query(value = "SELECT r.* FROM chat_rooms r " +
                   "WHERE r.type = 'DM' AND r.id IN (" +
                   "  SELECT room_id FROM chat_members WHERE user_id IN (:userId1, :userId2) " +
                   "  GROUP BY room_id HAVING COUNT(DISTINCT user_id) = 2" +
                   ") AND (" +
                   "  SELECT COUNT(*) FROM chat_members WHERE room_id = r.id" +
                   ") = 2", 
           nativeQuery = true)
    Optional<ChatRoom> findExistingDmRoom(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}