package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    // --- 既存メソッド (日付順 = デフォルト) ---
    List<ChatRoom> findAllByOrderByCreatedAtDesc();
    List<ChatRoom> findByTypeOrderByCreatedAtDesc(Integer type);

    @Query("SELECT r FROM ChatRoom r WHERE r.type = :type AND r.roomId IN (SELECT m.id.roomId FROM ChatMember m WHERE m.id.userId = :userId) ORDER BY r.createdAt DESC")
    List<ChatRoom> findJoinedRoomsByUserId(@Param("userId") Integer userId, @Param("type") Integer type);
    
    @Query("SELECT r FROM ChatRoom r WHERE r.type = 1 AND r.roomId IN (SELECT m.id.roomId FROM ChatMember m WHERE m.id.userId = :userId) ORDER BY r.createdAt DESC")
    List<ChatRoom> findDmRoomsByUserId(@Param("userId") Integer userId);

    // 既存の検索用メソッド (日付順)
    @Query("SELECT DISTINCT r FROM ChatRoom r JOIN ChatMember m ON r.roomId = m.id.roomId WHERE r.type = 2 AND m.id.userId = :userId AND r.name LIKE %:keyword% ORDER BY r.createdAt DESC")
    List<ChatRoom> searchJoinedGroupsByName(@Param("userId") Integer userId, @Param("keyword") String keyword);

    @Query("SELECT DISTINCT r FROM ChatRoom r JOIN ChatMember m1 ON r.roomId = m1.id.roomId LEFT JOIN ChatMember m2 ON r.roomId = m2.id.roomId LEFT JOIN Users u ON m2.id.userId = u.usersId WHERE r.type = 2 AND m1.id.userId = :userId AND (r.name LIKE %:keyword% OR u.name LIKE %:keyword%) ORDER BY r.createdAt DESC")
    List<ChatRoom> searchJoinedGroupsByNameOrMember(@Param("userId") Integer userId, @Param("keyword") String keyword);

    @Query("SELECT DISTINCT r FROM ChatRoom r LEFT JOIN ChatMember m ON r.roomId = m.id.roomId LEFT JOIN Users u ON m.id.userId = u.usersId WHERE r.type = 2 AND (r.name LIKE %:keyword% OR u.name LIKE %:keyword%) ORDER BY r.createdAt DESC")
    List<ChatRoom> searchAllGroupsByNameOrMember(@Param("keyword") String keyword);


    // ▼▼▼ ★今回追加: 名前順ソート用メソッド★ ▼▼▼

    // 管理者用: 全グループ (名前順)
    List<ChatRoom> findByTypeOrderByNameAsc(Integer type);

    // 一般ユーザー用: 参加グループ (名前順)
    @Query("SELECT r FROM ChatRoom r WHERE r.type = :type AND r.roomId IN (SELECT m.id.roomId FROM ChatMember m WHERE m.id.userId = :userId) ORDER BY r.name ASC")
    List<ChatRoom> findJoinedRoomsByUserIdOrderByName(@Param("userId") Integer userId, @Param("type") Integer type);

    // 検索用: Role=1 (名前順)
    @Query("SELECT DISTINCT r FROM ChatRoom r JOIN ChatMember m ON r.roomId = m.id.roomId WHERE r.type = 2 AND m.id.userId = :userId AND r.name LIKE %:keyword% ORDER BY r.name ASC")
    List<ChatRoom> searchJoinedGroupsByNameOrderByName(@Param("userId") Integer userId, @Param("keyword") String keyword);

    // 検索用: Role=2 (名前順)
    @Query("SELECT DISTINCT r FROM ChatRoom r JOIN ChatMember m1 ON r.roomId = m1.id.roomId LEFT JOIN ChatMember m2 ON r.roomId = m2.id.roomId LEFT JOIN Users u ON m2.id.userId = u.usersId WHERE r.type = 2 AND m1.id.userId = :userId AND (r.name LIKE %:keyword% OR u.name LIKE %:keyword%) ORDER BY r.name ASC")
    List<ChatRoom> searchJoinedGroupsByNameOrMemberOrderByName(@Param("userId") Integer userId, @Param("keyword") String keyword);

    // 検索用: Role=3 (名前順)
    @Query("SELECT DISTINCT r FROM ChatRoom r LEFT JOIN ChatMember m ON r.roomId = m.id.roomId LEFT JOIN Users u ON m.id.userId = u.usersId WHERE r.type = 2 AND (r.name LIKE %:keyword% OR u.name LIKE %:keyword%) ORDER BY r.name ASC")
    List<ChatRoom> searchAllGroupsByNameOrMemberOrderByName(@Param("keyword") String keyword);
}