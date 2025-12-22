package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.SurveyTarget;

@Repository
public interface SurveyTargetRepository extends JpaRepository<SurveyTarget, Integer> {
    // ★★★ 追加: アンケートIDで対象者を削除 ★★★
    void deleteBySurveyId(Integer surveyId);
}