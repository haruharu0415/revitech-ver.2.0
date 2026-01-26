package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.GroupMessage;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Integer> {
    // 古い順に取得（チャットログ用）
    List<GroupMessage> findByGroupIdOrderByCreatedAtAsc(Integer groupId);
}