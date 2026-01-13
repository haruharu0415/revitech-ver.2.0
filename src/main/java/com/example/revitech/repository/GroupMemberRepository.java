package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
    // グループ内のメンバー全取得
    List<GroupMember> findByGroupId(Integer groupId);
    
    // 特定のユーザーがグループにいるか確認
    Optional<GroupMember> findByGroupIdAndUserId(Integer groupId, Integer userId);
    
    // メンバー削除
    @Transactional
    void deleteByGroupIdAndUserId(Integer groupId, Integer userId);
}