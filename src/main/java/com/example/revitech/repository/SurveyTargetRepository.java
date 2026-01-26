package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.SurveyTarget;

@Repository
public interface SurveyTargetRepository extends JpaRepository<SurveyTarget, Integer> {

    // 特定のアンケートに紐づく対象者データを全て削除
    void deleteBySurveyId(Integer surveyId);
}