package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatMemberId;

public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {

    // 特定のルームIDに属するメンバーを取得
    // メソッド名で id.roomId を指定
    List<ChatMember> findByIdRoomId(Long roomId);

    // 特定のユーザーIDが属するメンバーシップを取得
    // メソッド名で id.userId を指定
    List<ChatMember> findByIdUserId(Long userId);

    // 特定のユーザーが特定のルームに存在するかチェック
    // メソッド名で id.userId と id.roomId を指定
    boolean existsByIdUserIdAndIdRoomId(Long userId, Long roomId);
}