package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ChatGroup;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Integer> {
}