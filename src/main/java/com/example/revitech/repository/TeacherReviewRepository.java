package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.TeacherReview;

@Repository
public interface TeacherReviewRepository extends JpaRepository<TeacherReview, Integer> {

    // --- ReviewService用 ---
    
    // 特定の先生のレビューを全て取得
    List<TeacherReview> findByTeacherId(Integer teacherId);

    // 管理者通知用: 開示請求中かつ未処理
    List<TeacherReview> findByIsDisclosureRequestedTrueAndIsDisclosureGrantedFalse();

    // 先生通知用: 開示許可済み
    List<TeacherReview> findByTeacherIdAndIsDisclosureGrantedTrue(Integer teacherId);

    // --- SurveyService用 ---
    void deleteBySurveyId(Integer surveyId);

    boolean existsBySurveyIdAndStudentId(Integer surveyId, Integer studentId);

    // ★追加: アンケートIDに紐づくレビュー（回答）を取得
    List<TeacherReview> findBySurveyId(Integer surveyId);
}