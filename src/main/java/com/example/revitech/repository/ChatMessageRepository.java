package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; 
// import org.springframework.data.repository.query.Param;

import com.example.revitech.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // ★★★ 修正箇所: room_id でメッセージを古い順に取得するメソッドに統一 ★★★
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);

    // 従来の findChatBetweenUsers(user1, user2) や findByReceiverStudentId(userId) は
    // 新しいルーム構造では不要になったため削除します。
}