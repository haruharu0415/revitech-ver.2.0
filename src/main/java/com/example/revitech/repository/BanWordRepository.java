package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.BanWord;

public interface BanWordRepository extends JpaRepository<BanWord, Long> {
    boolean existsByWordIgnoreCase(String word); // 大文字小文字区別せずに存在チェック
}