package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.TeacherReview;

@Repository
public interface TeacherReviewRepository extends JpaRepository<TeacherReview, Integer> {

    // 先生IDで検索 (作成日時の降順)
    List<TeacherReview> findByTeacherIdOrderByCreatedAtDesc(Integer teacherId);

    // アンケートIDで検索
    List<TeacherReview> findBySurveyId(Integer surveyId);

    // ★★★ エラー解消用: アンケートIDで削除 ★★★
    void deleteBySurveyId(Integer surveyId);

    // ★★★ エラー解消用: 特定の生徒がそのアンケートに回答済みかチェック ★★★
    boolean existsBySurveyIdAndStudentId(Integer surveyId, Integer studentId);

    // --- 開示請求関連 ---
    
    // 未処理の開示請求リストを取得
    @Query("SELECT r FROM TeacherReview r WHERE r.isDisclosureRequested = true AND (r.isDisclosureGranted = false OR r.isDisclosureGranted IS NULL)")
    List<TeacherReview> findPendingDisclosureRequests();

    // 未処理の開示請求の件数を取得
    @Query("SELECT COUNT(r) FROM TeacherReview r WHERE r.isDisclosureRequested = true AND (r.isDisclosureGranted = false OR r.isDisclosureGranted IS NULL)")
    long countPendingDisclosureRequests();

    // 先生用: 許可済み開示リストを取得
    List<TeacherReview> findByTeacherIdAndIsDisclosureGrantedTrue(Integer teacherId);

    // 先生用: 許可済み開示の件数を取得
    long countByTeacherIdAndIsDisclosureGrantedTrue(Integer teacherId);
}