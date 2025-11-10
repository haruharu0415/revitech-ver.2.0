package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatMemberId;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {

    // Spring Data JPAが 'id' (複合主キー) の中の 'roomId' を見て自動でクエリを生成してくれる
    List<ChatMember> findById_RoomId(Integer roomId);

    // 'id' の中の 'userId' を見てクエリを生成
    List<ChatMember> findById_UserId(Integer userId);

    // 'id' の中の 'userId' と 'roomId' の両方を見てクエリを生成
    boolean existsById_UserIdAndId_RoomId(Integer userId, Integer roomId);
}