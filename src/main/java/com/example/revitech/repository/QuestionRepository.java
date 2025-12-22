package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    
    // アンケートIDで検索
    List<Question> findBySurveyId(Integer surveyId);
    
    // アンケートIDで削除
    void deleteBySurveyId(Integer surveyId);

    // ★★★ 追加: 特定の先生(targetTeacherId)に向けられたアンケートの質問だけを取得する ★★★
    // Question -> Survey を surveyId で紐づけ、Survey の targetTeacherId で絞り込みます
    @Query("SELECT q FROM Question q WHERE q.surveyId IN (SELECT s.surveyId FROM Survey s WHERE s.targetTeacherId = :teacherId)")
    List<Question> findQuestionsByTargetTeacherId(@Param("teacherId") Integer teacherId);
}