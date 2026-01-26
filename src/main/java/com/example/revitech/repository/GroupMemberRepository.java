package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.GroupMember;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
    
    // ★この1行を追加してください！
    // "GroupId" と "UserId" の組み合わせが存在するかチェックする魔法のメソッド
    boolean existsByGroupIdAndUserId(Integer groupId, Integer userId);

    // ついでに削除用のメソッドも定義しておくと便利です
    void deleteByGroupIdAndUserId(Integer groupId, Integer userId);
}