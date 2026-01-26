package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.TeacherReview;

@Repository
public interface TeacherReviewRepository extends JpaRepository<TeacherReview, Integer> {

    // 既存のメソッド
    List<TeacherReview> findByTeacherIdOrderByCreatedAtDesc(Integer teacherId);
    List<TeacherReview> findByIsDisclosureRequestedTrueAndIsDisclosureGrantedFalse();
    List<TeacherReview> findByIsDisclosureGrantedTrue();
    List<TeacherReview> findBySurveyId(Integer surveyId);
    void deleteBySurveyId(Integer surveyId);
    boolean existsBySurveyIdAndStudentId(Integer surveyId, Integer studentId);

    // ★★★ 追加: これがないとエラーになります ★★★
    List<TeacherReview> findByTeacherId(Integer teacherId);
    
    // ★★★ 追加: 開示ステータスでの検索用 ★★★
    List<TeacherReview> findByDisclosureStatus(Integer status);
}