package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ReviewAnswer;

@Repository
public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Integer> {
    
    // 特定の質問IDに対する回答のリストを取得（平均計算用）
    List<ReviewAnswer> findByQuestionId(Integer questionId);
}