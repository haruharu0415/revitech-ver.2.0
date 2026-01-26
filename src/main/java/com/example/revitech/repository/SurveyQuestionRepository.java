package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.SurveyQuestion;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Integer> {
    // 質問順序カラムがないため、ID順で取得します
    List<SurveyQuestion> findBySurveyIdOrderByQuestionIdAsc(Integer surveyId);
}