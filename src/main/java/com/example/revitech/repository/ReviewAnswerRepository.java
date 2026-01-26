package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ReviewAnswer;

@Repository
public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Integer> {
    
    // 特定の先生への回答を取得
    List<ReviewAnswer> findByTeacherId(Integer teacherId);

    // 特定のアンケートかつ特定の生徒の回答詳細を取得
    List<ReviewAnswer> findBySurveyIdAndStudentId(Integer surveyId, Integer studentId);
    
    // アンケート削除用
    void deleteBySurveyId(Integer surveyId);
    
    // ★★★ 追加: レビューIDに紐づく回答詳細を取得 (SurveyResult詳細表示で使用) ★★★
    List<ReviewAnswer> findByReviewId(Integer reviewId);
}