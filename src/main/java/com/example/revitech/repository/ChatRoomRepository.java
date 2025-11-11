package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    @Query(value = "SELECT r.* FROM chat_rooms r " +
                   "JOIN chat_members m1 ON r.room_id = m1.room_id " +
                   "JOIN chat_members m2 ON r.room_id = m2.room_id " +
                   "WHERE r.type = 1 AND m1.users_id = :userId1 AND m2.users_id = :userId2",
           nativeQuery = true)
    Optional<ChatRoom> findDmRoomBetweenUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
}