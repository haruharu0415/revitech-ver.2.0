package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.revitech.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 2人のユーザー(users_id)が参加するDMルーム(typeがDMを示す値)を検索するクエリ (typeがIntegerの場合の例)
    @Query(value = "SELECT r.* FROM chat_rooms r " +
                   "JOIN chat_members m1 ON r.room_id = m1.room_id " +
                   "JOIN chat_members m2 ON r.room_id = m2.room_id " +
                   "WHERE r.type = 1 AND m1.users_id = :userId1 AND m2.users_id = :userId2", // typeの値は要確認
           nativeQuery = true)
    Optional<ChatRoom> findExistingDmRoom(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // 特定のユーザー(users_id)が作成したルームを取得 (creatarUserId 使用)
    List<ChatRoom> findByCreatarUserId(Long creatarUserId);
}