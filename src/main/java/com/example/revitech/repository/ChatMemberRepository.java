package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatMember;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Integer> {

    List<ChatMember> findByRoomId(Integer roomId);

    // ★ メソッド名を findByUserId に修正
    List<ChatMember> findByUserId(Integer userId);

    // ★ メソッド名を existsByUserIdAndRoomId に修正
    boolean existsByUserIdAndRoomId(Integer userId, Integer roomId);
}