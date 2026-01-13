package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatRoomMember;
import com.example.revitech.entity.key.ChatRoomMemberId;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, ChatRoomMemberId> {

    // 特定のルームのメンバー一覧を取得
    List<ChatRoomMember> findByRoomId(Integer roomId);
    
    // 特定のユーザーが特定のルームに参加しているか確認
    Optional<ChatRoomMember> findByRoomIdAndUserId(Integer roomId, Integer userId);
    
    // 退会処理などに使用（ルームIDとユーザーIDで削除）
    void deleteByRoomIdAndUserId(Integer roomId, Integer userId);
}