package com.example.revitech.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Integer roomId);

    long countByRoomIdAndCreatedAtAfter(Integer roomId, LocalDateTime timestamp);

    Optional<ChatMessage> findFirstByRoomIdOrderByCreatedAtDesc(Integer roomId);
}