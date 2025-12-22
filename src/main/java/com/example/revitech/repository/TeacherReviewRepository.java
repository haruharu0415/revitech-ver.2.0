package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.TeacherReview;

@Repository
public interface TeacherReviewRepository extends JpaRepository<TeacherReview, Integer> {
    
    List<TeacherReview> findByTeacherId(Integer teacherId);
    void deleteBySurveyId(Integer surveyId);
    boolean existsBySurveyIdAndStudentId(Integer surveyId, Integer studentId);
    List<TeacherReview> findByTeacherIdOrderByCreatedAtDesc(Integer teacherId);

    // ★★★ 追加: 管理者用 (開示請求中のもの全て) ★★★
    List<TeacherReview> findByDisclosureStatus(Integer disclosureStatus);

    // ★★★ 追加: 先生用 (自分宛ての開示済み かつ 未確認のもの) ★★★
    List<TeacherReview> findByTeacherIdAndDisclosureStatusAndTeacherChecked(Integer teacherId, Integer disclosureStatus, Integer teacherChecked);
}