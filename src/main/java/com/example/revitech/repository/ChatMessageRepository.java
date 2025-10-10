package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.revitech.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 送信者と受信者の間の全メッセージを取得（双方の送信を含む）
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.senderStudentId = :user1 AND m.receiverStudentId = :user2) OR " +
           "(m.senderStudentId = :user2 AND m.receiverStudentId = :user1) " +
           "ORDER BY m.createdAt ASC")
    List<ChatMessage> findChatBetweenUsers(@Param("user1") Long user1, @Param("user2") Long user2);

	List<ChatMessage> findByReceiverStudentId(Long userId);
}
