package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatReadStatus;

public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {
    Optional<ChatReadStatus> findByUserIdAndRoomId(Long userId, Long roomId);
}