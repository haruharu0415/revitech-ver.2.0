package com.example.revitech.repository;

import java.util.List;

// import java.util.UUID; // ★ UUID は使わない
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatMemberId; // ★ 主キー型 (Long, Long)

// ★ 主キー型は ChatMemberId (Long, Long) なので変更なし ★
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {

    // ★ userId の型を Long に戻す ★
    List<ChatMember> findByIdUserId(Long userId);

    // ★ userId の型を Long に戻す ★
    boolean existsByIdUserIdAndIdRoomId(Long userId, Long roomId);
}