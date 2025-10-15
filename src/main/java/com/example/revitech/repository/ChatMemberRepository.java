package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatMember;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    
    List<ChatMember> findByRoomId(Long roomId);
    
    List<ChatMember> findByUserId(Long userId);

    // 指定されたuserIdとroomIdの組み合わせが存在するかどうかをチェックする
    boolean existsByUserIdAndRoomId(Long userId, Long roomId);
}