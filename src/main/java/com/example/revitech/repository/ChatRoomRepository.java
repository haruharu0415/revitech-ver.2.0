package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatRoom;

// ★ ChatRoom の主キー型 (room_id) は Long なので変更なし ★
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    // (UUID に関連する変更はなかったので、ここにあるカスタムメソッドもそのままのはず)
    // 例:
    // @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m1 JOIN cr.members m2 " +
    //        "WHERE cr.type = 1 AND m1.user.id = :userId1 AND m2.user.id = :userId2")
    // Optional<ChatRoom> findDmRoomByUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}