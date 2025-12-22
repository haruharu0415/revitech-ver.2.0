package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {
    // 先生が作成したアンケート一覧
    List<Survey> findByTeacherIdOrderByCreatedAtDesc(Integer teacherId);

    // 生徒が対象になっているアンケート一覧を取得するカスタムクエリ
    @Query("SELECT s FROM Survey s JOIN SurveyTarget t ON s.surveyId = t.surveyId WHERE t.studentId = :studentId ORDER BY s.createdAt DESC")
    List<Survey> findByTargetStudentId(@Param("studentId") Integer studentId);
}