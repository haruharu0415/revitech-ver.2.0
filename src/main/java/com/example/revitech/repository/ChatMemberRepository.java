package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ChatMember;
import com.example.revitech.entity.ChatMemberId;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {

    // 指定した部屋のメンバー一覧を取得
    List<ChatMember> findById_RoomId(Integer roomId);

    // 指定したユーザーが参加している部屋一覧を取得
    List<ChatMember> findById_UserId(Integer userId);

    // ユーザーがその部屋に参加しているかチェック
    boolean existsById_UserIdAndId_RoomId(Integer userId, Integer roomId);

    // ★★★ 追加: 指定した部屋のメンバーを全員削除 ★★★
    void deleteById_RoomId(Integer roomId);
}