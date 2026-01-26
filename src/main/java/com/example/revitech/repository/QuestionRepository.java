package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    // 特定のアンケートに紐づく質問を全て取得
    List<Question> findBySurveyId(Integer surveyId);

    // 特定のアンケートに紐づく質問を全て削除（アンケート削除時用）
    void deleteBySurveyId(Integer surveyId);
}