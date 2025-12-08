package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    // DMの既存ルーム検索（既存のメソッド）
    @Query("SELECT r FROM ChatRoom r " +
           "JOIN ChatMember m1 ON r.roomId = m1.id.roomId " +
           "JOIN ChatMember m2 ON r.roomId = m2.id.roomId " +
           "WHERE r.type = 1 AND m1.id.userId = :userId1 AND m2.id.userId = :userId2")
    Optional<ChatRoom> findDmRoomBetweenUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    /**
     * ★★★ ここから追加 ★★★
     * 自分が参加しているグループチャットを取得 (type = 2)
     */
    @Query("SELECT r FROM ChatRoom r, ChatMember m WHERE r.roomId = m.id.roomId AND m.id.userId = :userId AND r.type = 2")
    List<ChatRoom> findGroupRoomsByUserId(@Param("userId") Integer userId);

    /**
     * 自分が参加しているDMルームを取得 (type = 1)
     */
    @Query("SELECT r FROM ChatRoom r, ChatMember m WHERE r.roomId = m.id.roomId AND m.id.userId = :userId AND r.type = 1")
    List<ChatRoom> findDmRoomsByUserId(@Param("userId") Integer userId);
}