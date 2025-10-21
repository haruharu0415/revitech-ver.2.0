package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatReadStatus;

@Repository
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Integer> {

    // ★ メソッド名を findByUserIdAndRoomId に修正
    Optional<ChatReadStatus> findByUserIdAndRoomId(Integer userId, Integer roomId);
}