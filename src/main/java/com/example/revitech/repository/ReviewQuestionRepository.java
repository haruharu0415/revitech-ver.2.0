package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ReviewQuestion;

@Repository
public interface ReviewQuestionRepository extends JpaRepository<ReviewQuestion, Integer> {
    // 基本的なCRUD機能があれば十分なので、追加メソッドは特に不要です
}