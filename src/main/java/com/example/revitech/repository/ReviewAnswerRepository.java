package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ReviewAnswer;

@Repository
public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Integer> {
    
    // 特定の先生への回答を取得（既存機能で利用しているはず）
    List<ReviewAnswer> findByTeacherId(Integer teacherId);

    // ★追加: 特定のアンケートかつ特定の生徒の回答詳細を取得
    List<ReviewAnswer> findBySurveyIdAndStudentId(Integer surveyId, Integer studentId);
    
    // アンケート削除用（既存機能にあるはず）
    void deleteBySurveyId(Integer surveyId);
}